module MultiSet
( MSet(..),
  empty, 
  add, 
  addAll,
  occs, 
  elems,
  subeq, 
  union, 
  mapMSet
) where

data MSet a = MS [(a, Int)] deriving (Show)

-- Constructor for an empty MSet
empty :: MSet a
empty = MS []

-- Helper function to insert an element v with the multiplicity m into the multiset
insertWMultiplicity :: Eq a => a -> Int -> MSet a -> MSet a
insertWMultiplicity v m (MS l) = MS $ aux l v m
  where
    -- If the element v is not present, add it with the given multiplicity
    aux [] v m = [(v, m)]
    aux ((e, c):t) v m
      -- If the element v is already present, update the multiplicity with the sum of the current and input m
      | e == v = (e, c + m) : t
      -- Continue to search
      | otherwise = (e, c) : aux t v m

-- Add the value to the multiset 
-- Use the helper function to insert the given element with a multiplicity of 1
add :: Eq a => MSet a -> a -> MSet a
add (MS l) v = insertWMultiplicity v 1 (MS l)

-- Add all values of input foldable to the multiset 
-- Utilize the 'foldl' function to sequentially apply the 'add' function to each element and accumulator,
-- using the input multiset as the starting accumulator
addAll :: (Foldable t, Eq a) => MSet a -> t a -> MSet a
addAll multiset = foldl add multiset

-- Count occurrences of a specific value in the multiset 
occs :: Eq a => MSet a -> a -> Int
occs (MS l) v = case filter (\(e, _) -> v == e) l of
                  -- Evaluate the filter first 
                  -- No value found indicates that the multiplicity of v is 0
                  [] -> 0
                  -- Given the property of a multiset, if a value is present, there should be only one pair, so take its multiplicity
                  ((_, c):_)  -> c

-- Get a list of all unique elements in the multiset 
elems :: MSet a -> [a]
elems (MS mset) = map fst mset

-- Check if one MSet is a sub-multiset of another
-- For each element in l1, check if it is in (MS l2) with at least the same multiplicity
-- It uses the 'all' function with a predicate that checks that each element in l1 
-- is in (MS l2) with at least the same multiplicity
subeq :: Eq a => MSet a -> MSet a -> Bool
subeq (MS l1) (MS l2) = all (\(e1, c1) -> occs (MS l2) e1 >= c1) l1

-- Union of two multisets, combining multiplicities
-- Add each element of ms2 with its multiplicity to ms1 using foldl and ms1 as initial
-- accumulator value
-- The `uncurry` operator is necessary to transform the function `insertWMultiplicity` (which takes two arguments 
-- for value and multiplicity) into a function that takes a tuple for value and multiplicity.
-- The 'flip' operator is used to swap the arguments, enabling the use of 'foldl' instead of 'foldr'
union :: Eq a => MSet a -> MSet a -> MSet a
union ms1 (MS l2) = foldl (flip $ uncurry insertWMultiplicity) ms1 l2

-- Instance declaration for equality of MSet
-- It states that two multisets are considered equal if each is a sub-multiset of the other. 
-- In other words, they have the same elements with the same multiplicities, regardless of the order.
instance Eq a => Eq (MSet a) where
  (==) :: (Eq a) => MSet a -> MSet a -> Bool
  (==) ms1 ms2 = subeq ms2 ms1 && subeq ms1 ms2


-- Instance declaration for Foldable type class for MSet
-- Defined using the minimal complete definition of Foldable with 'foldr', as specified in the documentation of Foldable
-- 'foldr' is defined by applying the provided function 'f' to the elements of the MSet while ignoring their multiplicities, 
-- and starting with the initial accumulator 'ini'.
instance Foldable MSet where
  foldr :: (a -> b -> b) -> b -> MSet a -> b
  foldr f ini (MS l1) =  foldr (f . fst) ini l1

-- The function `f` maps each value of type `a` to a fixed value of type `b`, preserving the multiplicity.
-- I use the function `insertWMultiplicity` composed with the function `f` to create a function that first transforms 
-- the value using `f` and then inserts it into the multiset with the same multiplicity.
-- The `uncurry` operator is necessary to transform the function obtained by composing `insertWMultiplicity` and `f` 
-- (which takes two arguments for value and multiplicity) into a function that takes a tuple for value and multiplicity.
-- The 'flip' operator is used to swap the arguments, enabling the use of 'foldl' instead of 'foldr'
mapMSet :: Eq b => (a -> b) -> MSet a -> MSet b
mapMSet f (MS l) = foldl (flip $ uncurry $ insertWMultiplicity . f) empty l

-- It's not feasible to use the 'mapMSet' function as an implementation for 'fmap' in the Functor instance.
-- This is because 'mapMSet' imposes a constraint on the returned type (requiring it to implement 'Eq'), which is not present or required in the 'fmap' signature.
-- The Functor instance for 'MSet' expects a function of type 'a -> b', but the constraint on 'Eq b' in 'mapMSet' introduces additional requirements that do not align with the Functor definition.
-- 
-- instance Functor MSet where 
--   fmap = mapMSet:}
