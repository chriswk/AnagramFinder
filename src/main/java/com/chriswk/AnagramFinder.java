package com.chriswk;

import java.io.*;
import java.text.Collator;
import java.util.*;

/**
 * Hello world!
 */
public class AnagramFinder {

    private static final Locale norwegianLocale = new Locale("Norway", "no");
    private static final Collator norwegianCollator = Collator.getInstance(norwegianLocale);
    public AnagramFinder() {
    }

    public static void main(String[] args) {

        AnagramFinder af = new AnagramFinder();
        af.findAnagrams();


    }

    public void findAnagrams() {
        File eventyrFile = new File(ClassLoader.getSystemResource("eventyr.txt").getFile());
        File dictionary = new File(ClassLoader.getSystemResource("thesaurus.txt").getFile());
        List<String> inputWordList = buildWordList(eventyrFile);
        Map<String, SortedSet<String>> candidateMap = buildCandidateMap(dictionary);
        printAnagrams(inputWordList, candidateMap);

    }

    private List<String> buildWordList(File inputFile) {
        List<String> wordList = new ArrayList<String>();
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
            Scanner scanner = new Scanner(isr);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                wordList.add(word);
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("Could not find file");
        } catch (IOException ioEx) {
            System.err.println("IOexception while building work map");
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException ioEx) {
                System.err.println("Couldn't close the InputStream");
            }
        }
        Collections.sort(wordList, norwegianCollator);
        return wordList;
    }

    private Map<String, SortedSet<String>> buildCandidateMap(File dictionary) {
        Map<String, SortedSet<String>> candidateMap = new HashMap<String, SortedSet<String>>();
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(dictionary), "UTF-8");
            Scanner scanner = new Scanner(isr);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().toLowerCase(norwegianLocale);
                String normalizedWord;
                if (word.length() > 1) {
                    normalizedWord = normalizeWord(word);
                } else {
                    normalizedWord = word;
                }
                if (candidateMap.containsKey(normalizedWord)) {
                    SortedSet<String> currentAnagramSet = candidateMap.get(normalizedWord);
                    if (!currentAnagramSet.contains(word)) {
                        currentAnagramSet.add(word);
                    }
                } else {
                    SortedSet<String> anagramSet = new TreeSet<String>(norwegianCollator);
                    anagramSet.add(word);
                    candidateMap.put(normalizedWord, anagramSet);
                }
            }

        } catch (FileNotFoundException fnfe) {
            System.err.println("Could not find file");
        } catch (IOException ioEx) {
            System.err.println("IOexception while building work map");
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException ioEx) {
                System.err.println("Couldn't close the InputStream");
            }
        }
        return candidateMap;
    }

    private void printAnagrams(List<String> inputWordList, Map<String, SortedSet<String>> dictionary) {
        for(String word : inputWordList) {
            //No point in checking for anagrams for words of length 1
            StringBuilder anagramOutput = new StringBuilder(word);
            if(word.length() > 1) {
                String normalizedWord = normalizeWord(word);
               if(dictionary.containsKey(normalizedWord)) {
                    anagramOutput.append(" -");
                    SortedSet<String> anagrams = dictionary.get(normalizedWord);
                    for(String anagram : anagrams) {
                        anagramOutput.append(" ")
                        .append(anagram);
                    }
                   System.out.println(anagramOutput.toString());
                }
            }

        }

    }

    private String normalizeWord(String word) {
        char[] normalizedChar = word.toCharArray();
        Arrays.sort(normalizedChar); //make sure we normalize using norwegian rules
        return new String(normalizedChar);
    }
}
