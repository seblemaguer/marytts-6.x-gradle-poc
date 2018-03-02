package marytts.io.serializer;

import marytts.data.item.phonology.Phoneme;
import marytts.data.item.linguistic.Word;
import marytts.data.Sequence;
import marytts.data.Relation;
import marytts.features.FeatureMap;
import marytts.features.Feature;
import marytts.data.Utterance;
import marytts.io.MaryIOException;
import marytts.data.SupportedSequenceType;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.io.File;

/**
 * Feature serializer to generate TSV format output. There is not import from it
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le
 *         Maguer</a>
 */
public class PhoneCSVSerializer implements Serializer {

    /** The constant for representing the tab separator */
    protected static final String SEP = "\t";

    /**
     * Constructor
     *
     */
    public PhoneCSVSerializer() {
    }

    /**
     * Generate the TSV output from the utterance. Only the feature sequence is
     * used !
     *
     * @param utt
     *            the utterance containing the feature sequence
     * @return the TSV formatted feature sequence
     * @throws MaryIOException
     *             if anything is going wrong
     */
    public Object export(Utterance utt) throws MaryIOException {
        if (!utt.hasSequence(SupportedSequenceType.WORD)) {
            throw new MaryIOException("Current utterance doesn't have any Words. Check the module sequence",
                                      null);
        }
        if (!utt.hasSequence(SupportedSequenceType.SYLLABLE)) {
            throw new MaryIOException("Current utterance doesn't have any Syllables. Check the module sequence",
                                      null);
        }
        if (!utt.hasSequence(SupportedSequenceType.PHONE)) {
            throw new MaryIOException("Current utterance doesn't have any Phones. Check the module sequence",
                                      null);
        }

        Sequence<Word> seq_words = (Sequence<Word>) utt.getSequence(SupportedSequenceType.WORD);
	Relation rel_word_syl = utt.getRelation(SupportedSequenceType.WORD, SupportedSequenceType.SYLLABLE);
	Relation rel_syl_phone = utt.getRelation(SupportedSequenceType.SYLLABLE, SupportedSequenceType.PHONE);

	String output = "";
	int s=0;
	for (int w=0; w<seq_words.size(); w++)  {
	    output += seq_words.get(w).getText() + SEP;

	    int[] syl_indexes = rel_word_syl.getRelatedIndexes(w);

	    for (int i_s=0; i_s<syl_indexes.length; i_s++) {
		s = syl_indexes[i_s];

		ArrayList<Phoneme> phones = (ArrayList<Phoneme>) rel_syl_phone.getRelatedItems(s);
		for (int i_p=0; i_p<phones.size(); i_p++) {
		    Phoneme ph = phones.get(i_p);
		    output += ph.getLabel();

		    if (i_p != (phones.size()-1))
			output += " ";
		}

		if (i_s < syl_indexes.length-1)
		    output += ", ";
	    }

	    output += "\n";
	}

	return output;
    }

    /**
     * Unsupported operation ! We can't import from a TSV formatted input.
     *
     * @param content
     *            unused
     * @return nothing
     * @throws MaryIOException
     *             never done
     */
    public Utterance load(String content) throws MaryIOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Encapsulation to get the value given the key of the feature map
     *
     * @param feature_map
     *            the feature map
     * @param feature_name
     *            the feature name
     * @return the value of the feature or an empty string if not defined
     */
    protected final String getValue(FeatureMap feature_map, String feature_name) {
        return feature_map.get(feature_name) == null ? "" : feature_map.get(feature_name).getStringValue();
    }
}

/* HTSLabelSerializer.java ends here */
