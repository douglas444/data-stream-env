package br.com.douglas444.datastreamenv.common;

public class InterceptionResult {

    private ConceptCategory realCategory;
    private ConceptCategory frameworkPrediction;
    private ConceptCategory indicatorPrediction;

    public InterceptionResult(ConceptCategory realCategory,
                              ConceptCategory frameworkPrediction,
                              ConceptCategory indicatorPrediction) {

        this.realCategory = realCategory;
        this.frameworkPrediction = frameworkPrediction;
        this.indicatorPrediction = indicatorPrediction;
    }

    public ConceptCategory getRealCategory() {
        return realCategory;
    }

    public void setRealCategory(ConceptCategory realCategory) {
        this.realCategory = realCategory;
    }

    public ConceptCategory getFrameworkPrediction() {
        return frameworkPrediction;
    }

    public void setFrameworkPrediction(ConceptCategory frameworkPrediction) {
        this.frameworkPrediction = frameworkPrediction;
    }

    public ConceptCategory getIndicatorPrediction() {
        return indicatorPrediction;
    }

    public void setIndicatorPrediction(ConceptCategory indicatorPrediction) {
        this.indicatorPrediction = indicatorPrediction;
    }

}
