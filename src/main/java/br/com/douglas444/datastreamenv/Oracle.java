package br.com.douglas444.datastreamenv;

import br.com.douglas444.mltk.datastructure.Sample;

class Oracle {

    static Integer label(final Sample sample) {
        return sample.getY();
    }

}
