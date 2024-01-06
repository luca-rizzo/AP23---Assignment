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

insertWMultiplicity :: Eq a => a -> Int -> MSet a -> MSet a
insertWMultiplicity v m (MS l) = MS $ aux l v m
  where
    aux [] v m = [(v, m)]
    aux ((e, c):t) v m
      | e == v = (e, c + m) : t
      | otherwise = (e, c) : aux t v m

add :: Eq a => MSet a -> a -> MSet a
add (MS l) v = insertWMultiplicity v 1 (MS l)

addAll :: (Foldable t, Eq a) => MSet a -> t a -> MSet a
addAll (MS l) = foldl add (MS l)

occs :: Eq a => MSet a -> a -> Int
occs (MS l) v = case filter (\(e, c) -> v == e) l of
                  [] -> 0
                  ((e, c):_) -> c

elems :: MSet a -> [a]
elems (MS mset) = map fst mset

-- Check if one MSet is a sub-multiset of another
-- For each element in l1, check if it is in (MS l2) with at least the same multiplicity
subeq :: Eq a => MSet a -> MSet a -> Bool
subeq (MS l1) (MS l2) = all (\(e1, c1) -> occs (MS l2) e1 >= c1) l1
    

union :: Eq a => MSet a -> MSet a -> MSet a
union (MS l1) (MS l2) = foldl (flip $ uncurry insertWMultiplicity) (MS l1) l2

instance Eq a => Eq (MSet a) where
  (==) ms1 ms2 = subeq ms2 ms1 && subeq ms1 ms2

instance Foldable MSet where
  foldr f ini (MS l1) =  foldr (f . fst) ini l1

-- The function `f` maps each value of type `a` to a fixed value of type `b`, preserving the multiplicity.
-- I use the function `insertWMultiplicity` composed with the function `f` to create a function that first transforms the value using `f` and then inserts it into the multiset with the same multiplicity.
-- The `uncurry` operator is necessary to transform the function obtained by composing `insertWMultiplicity` and `f` (which takes two arguments for value and multiplicity) into a function that takes a tuple for value and multiplicity.

mapMSet :: Eq b => (a -> b) -> MSet a -> MSet b
mapMSet f (MS l) = foldl (flip $ uncurry $ insertWMultiplicity . f) empty l

--It's not feasible to use the 'mapMSet' function as an implementation for 'fmap' in the Functor instance.
--This is because 'mapMSet' imposes a constraint on the returned type (requiring it to implement 'Eq'), which is not present or required in the 'fmap' signature.
--The Functor instance for 'MSet' expects a function of type 'a -> b', but the constraint on 'Eq b' in 'mapMSet' introduces additional requirements that do not align with the Functor definition.
-- 
-- instance Functor MSet where 
--   fmap = mapMSet:}
