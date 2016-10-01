/*
 * Copyright (c) 2015 The University of Auckland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors: Ping Zhao (pzha291@aucklanduni.ac.nz)
 * Created: 2015-06-10
 * Last Updated: --
 */
package edu.uoa.cs.master.cloudmanufacturingnlp.dictionary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import edu.uoa.cs.master.cloudmanufacturingnlp.util.Constants;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.IndexWordSet;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

/**
 * @author pingz
 *
 */
public class JwnlWordNet {

	private final static JwnlWordNet instance = new JwnlWordNet();
	private Dictionary dictionary = null;

	private JwnlWordNet() {
		Constants.LOCAL_BASE = Constants.FilePath.LOCAL_BASE_JAVA;

		try {
			final FileInputStream in = new FileInputStream(Constants.FilePath.WORDNET_PROPERTY);
			JWNL.initialize(in);
			this.dictionary = Dictionary.getInstance();

		} catch (final FileNotFoundException | JWNLException e) {
			e.printStackTrace();
		}
	}

	public static JwnlWordNet getInstance() {
		return instance;
	}

	/**
	 * Lookup the synonym in the WordNet.
	 *
	 * IndexWordSet:
	 * <p>
	 * [IndexWordSet: [IndexWord: [Lemma: share] [POS: verb]][IndexWord: [Lemma: share] [POS: noun]]]
	 * </p>
	 *
	 * @param originalLemma
	 * @return
	 */
	public Set<String> lookupSynonym(final String originalLemma, final POS pos) {
		final Set<String> synonyms = new HashSet<>();

		try {
			// [IndexWordSet: [IndexWord: [Lemma: share] [POS: verb]][IndexWord: [Lemma: share] [POS: noun]]]
			final IndexWordSet indexWordSet = this.dictionary.lookupAllIndexWords(originalLemma);
			for (final IndexWord indexWord : indexWordSet.getIndexWordArray()) {
				// [Synset: [Offset: 2295208] [POS: verb] Words: partake, share, partake_in -- (have, give, or receive a share of; "We
				// shared the cake")]
				final Synset[] synsets = indexWord.getSenses();
				for (final Synset synset : synsets) {
					for (final Word word : synset.getWords()) {
						if (pos == null || (pos.equals(word.getPOS()))) {
							synonyms.add(word.getLemma());
						}
					}
				}
			}
		} catch (final JWNLException e) {
			e.printStackTrace();
		}

		return synonyms;
	}

	/**
	 * @param <E>
	 * @param dictionary
	 * @throws JWNLException
	 */
	private IndexWordSet lookupSynsetThroughSenses(final String lemma) throws JWNLException {
		// doesn't subject lemma to any morphological processing
		// final IndexWord getIndexWord = dictionary.getIndexWord(POS.VERB, "shares");
		// System.out.println(getIndexWord);

		// subject lemma to any morphological processing
		// final IndexWord lookupIndexWord = dictionary.lookupIndexWord(POS.VERB, "shares");
		// System.out.println(lookupIndexWord);

		final IndexWordSet indexWordSet = this.dictionary.lookupAllIndexWords(lemma);
		System.out.println(indexWordSet);

		// iterate each index word, and then synset
		for (final IndexWord indexWord : indexWordSet.getIndexWordArray()) {
			final Synset[] synsets = indexWord.getSenses();
			for (final Synset synset : synsets) {
				printSynsetAndLemma(synset);
			}
		}

		return indexWordSet;
	}

	/**
	 * @param dictionary
	 * @return
	 * @throws JWNLException
	 */
	// private IndexWord lookupSynsetThroughOffset() throws JWNLException {
	// IndexWord indexWord = this.dictionary.lookupIndexWord(POS.NOUN, "shares");
	// for (final long offset : indexWord.getSynsetOffsets()) {
	// final Synset synsetNoun = this.dictionary.getSynsetAt(POS.NOUN, offset);
	// printSynsetAndLemma(synsetNoun);
	// }
	//
	// indexWord = this.dictionary.lookupIndexWord(POS.VERB, "shares");
	// for (final long offset : indexWord.getSynsetOffsets()) {
	// final Synset synsetVerb = this.dictionary.getSynsetAt(POS.VERB, offset);
	// printSynsetAndLemma(synsetVerb);
	// }
	//
	// return indexWord;
	// }

	/**
	 * @param synset
	 */
	private void printSynsetAndLemma(final Synset synset) {
		System.out.println(synset);
		for (final Word word : synset.getWords()) {
			System.out.println(word.getLemma());
		}
		System.out.println();
	}

	public static void main(final String[] args) throws JWNLException {

		final JwnlWordNet jwnlWordNet = JwnlWordNet.getInstance();

		// lookup synset through senses
		final IndexWordSet indexWordSet = jwnlWordNet.lookupSynsetThroughSenses("part");
		System.out.println(indexWordSet.getSenseCount(POS.NOUN));
		System.out.println(indexWordSet.getSenseCount(POS.VERB));

		// lookup synset through offset
		// final IndexWord indexWord = jwnlWordNet.lookupSynsetThroughOffset();
		// System.out.println(indexWord.getSenseCount());

		final Set<String> synonyms = jwnlWordNet.lookupSynonym("Business Entity", null);
		System.out.println(synonyms);
	}
}
