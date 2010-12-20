package com.chriswk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.*;

/**
 * Hello world!
 */
public class AnagramFinder {

    private static final Locale norwegianLocale = new Locale("Norway", "no");
    private static final Collator norwegianCollator = Collator.getInstance(norwegianLocale);
    private static final String utf8Encoding = "UTF-8";

    public AnagramFinder() {
    }

    public static void main(String[] args) {

        AnagramFinder af = new AnagramFinder();
        af.findAnagrams();


    }

    public void findAnagrams() {
        InputStreamReader eventyrStream = null;
        InputStreamReader dictionaryStream = null;
        try {
            eventyrStream = new InputStreamReader(getClass().getResourceAsStream("/eventyr.txt"), utf8Encoding);
            dictionaryStream = new InputStreamReader(getClass().getResourceAsStream("/thesaurus.txt"), utf8Encoding);
            List<String> inputWordList = buildWordList(eventyrStream);
            Map<String, SortedSet<String>> candidateMap = buildCandidateMap(dictionaryStream);
            printAnagrams(inputWordList, candidateMap);
        } catch (UnsupportedEncodingException unSupp) {
            System.err.println("Unsupported encoding");
        } finally {
            try {
                if (eventyrStream != null) {
                    eventyrStream.close();
                }
                if (dictionaryStream != null) {
                    dictionaryStream.close();
                }
            } catch (IOException ioEx) {
                System.err.println("IOException while closing files");
            }
        }
    }

    private List<String> buildWordList(InputStreamReader wordListStream) {
        List<String> wordList = new ArrayList<String>();
        Scanner scanner = new Scanner(wordListStream);
        while (scanner.hasNextLine()) {
            String word = scanner.nextLine();
            wordList.add(word);
        }

        Collections.sort(wordList, norwegianCollator);
        return wordList;
    }

    private Map<String, SortedSet<String>> buildCandidateMap(InputStreamReader dictionaryStream) {
        Map<String, SortedSet<String>> candidateMap = new HashMap<String, SortedSet<String>>();
        Scanner scanner = new Scanner(dictionaryStream);
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

        return candidateMap;
    }

    private void printAnagrams(List<String> inputWordList, Map<String, SortedSet<String>> dictionary) {
        for (String word : inputWordList) {
            //No point in checking for anagrams for words of length 1
            StringBuilder anagramOutput = new StringBuilder(word);
            if (word.length() > 1) {
                String normalizedWord = normalizeWord(word);
                if (dictionary.containsKey(normalizedWord)) {
                    anagramOutput.append(" -");
                    SortedSet<String> anagrams = dictionary.get(normalizedWord);
                    for (String anagram : anagrams) {
                        if (!anagram.equals(word)) {
                            anagramOutput.append(" ")
                                    .append(anagram);
                        }
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
