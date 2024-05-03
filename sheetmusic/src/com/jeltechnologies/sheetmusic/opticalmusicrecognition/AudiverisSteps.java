package com.jeltechnologies.sheetmusic.opticalmusicrecognition;

import com.jeltechnologies.utils.datatypes.NamedValueCollection;

public class AudiverisSteps {
    private final NamedValueCollection steps = new NamedValueCollection();

    public AudiverisSteps() {
	init();
    }

    private void init() {
	steps.put("LOAD", "Get the sheet gray picture");
	steps.put("BINARY", "Binarize the sheet gray picture");
	steps.put("SCALE", "Compute sheet line thickness, interline, beam thickness");
	steps.put("GRID", "Retrieve staff lines, barlines, systems & parts");
	steps.put("HEADERS", "Retrieve Clef-Key-Time systems headers");
	steps.put("STEM_SEEDS", "Retrieve stem thickness & seeds for stems");
	steps.put("BEAMS", "Retrieve beams");
	steps.put("LEDGERS", "Retrieve ledgers");
	steps.put("HEADS", "Retrieve note heads");
	steps.put("STEMS", "Retrieve stems connected to heads & beams");
	steps.put("REDUCTION", "Reduce conflicts in heads, stems & beams");
	steps.put("CUE_BEAMS", "Retrieve cue beams");
	steps.put("TEXTS", "Call OCR on textual items");
	steps.put("MEASURES", "Retrieve raw measures from groups of barlines");
	steps.put("CHORDS", "Gather notes heads into chords");
	steps.put("CURVES", "Retrieve slurs, wedges & endings");
	steps.put("SYMBOLS", "Retrieve fixed-shape symbols");
	steps.put("LINKS", "Link and reduce symbols");
	steps.put("RHYTHMS", "Handle rhythms within measures");
	steps.put("PAGE", "Connect systems within page");
    }
    
    public String getStep(String key) {
	return steps.getFirst(key);
    }
}
