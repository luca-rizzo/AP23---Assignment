module TestMultiSet(
  readMSet,
  writeMSet
) where

import MultiSet (MSet(..), empty, addAll, union, elems)
import Data.List (sort)
import Text.Printf (printf)
import Data.Char (toLower)

-- Reads a file, returning an MSet of strings.
readMSet :: FilePath -> IO (MSet String)
readMSet filename = do 
  -- Read the file content as a string
  fileContent <- readFile filename
  -- Transforms all words in the file to their "ciao" representation
  let ciaoWord = map toCiao $ words fileContent
  -- Add all words to an MSet
  let mSet = addAll empty ciaoWord
  return mSet

-- Writes an MSet of strings to a file.
writeMSet :: MSet String -> FilePath -> IO ()
writeMSet mSet filename = do 
  -- Extract the list from the MSet using pattern matching
  let listMSet = case mSet of MS l -> l
  -- Format all tuples in the list as strings
  let formattedString = map (uncurry formatElement) listMSet
  -- Write all strings to the file, each on a new line
  writeFile filename $ unlines formattedString

-- Formats a (word, frequency) pair as a string
formatElement :: String -> Int -> String
formatElement = printf "%s - %d"

-- Transforms a string to lowercase and sorts it
toCiao :: String -> String
toCiao = sort . map toLower


main :: IO ()
main = do
  m1 <- readMSet "aux_files/anagram.txt"
  m2 <- readMSet "aux_files/anagram-s1.txt"
  m3 <- readMSet "aux_files/anagram-s2.txt"
  m4 <- readMSet "aux_files/margana2.txt"
  if m1 == union m2 m3
    then putStrLn "TEST-1: m1 is equal to the union of m2 and m3"
    else putStrLn "TEST-1: m1 not is equal to the union of m2 and m3"
  if m1 /= m4 && elems m1 == elems m4
    then putStrLn "TEST-2: m1 is not equal to m4 but they have the same elements"
    else putStrLn "TEST-2: m1 is equal to m4 or they have different elements"
  -- Writing multisets to files
  writeMSet m1 "output/anag-out.txt"
  writeMSet m2 "output/gana-out.txt"