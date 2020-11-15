package br.com.douglas444.datastreamenv.common;

import java.util.List;

public class ExperimentResult {

    private int actualNovelties;
    private int actualKnown;
    private int goodDisagreement;
    private int badDisagreement;
    private int goodAgreement;
    private int badAgreement;
    private int goodAbstention;
    private int badAbstention;
    private int indicatorTrueNovelty = 0;
    private int indicatorFalseNovelty = 0;
    private int indicatorTrueKnown = 0;
    private int indicatorFalseKnown = 0;
    private int indicatorNotClassifiedKnown = 0;
    private int indicatorNotClassifiedNovelty = 0;
    private int frameworkTrueNovelty = 0;
    private int frameworkFalseNovelty = 0;
    private int frameworkTrueKnown = 0;
    private int frameworkFalseKnown = 0;
    private int frameworkNotClassifiedKnown = 0;
    private int frameworkNotClassifiedNovelty = 0;

    public ExperimentResult(final List<InterceptionResult> results) {

        if (results.isEmpty()) {
            throw new IllegalArgumentException();
        }


        //Count actual novelties
        int noveltyCount = 0;
        for (InterceptionResult result : results) {
            if (result.getRealCategory() == ConceptCategory.NOVELTY) {
                ++noveltyCount;
            }
        }
        this.setActualNovelties(noveltyCount);
        this.setActualKnown(results.size() - noveltyCount);


        //Count good and bad disagreements
        int goodDisagreement = 0;
        int badDisagreement = 0;
        for (InterceptionResult result : results) {
            if (result.getFrameworkPrediction() != result.getIndicatorPrediction()
                    && result.getIndicatorPrediction() != ConceptCategory.NULL) {
                if (result.getRealCategory() != result.getFrameworkPrediction()) {
                    ++goodDisagreement;
                } else {
                    ++badDisagreement;
                }
            }
        }
        this.setGoodDisagreement(goodDisagreement);
        this.setBadDisagreement(badDisagreement);


        //Count good and bad agreements
        int goodAgreement = 0;
        int badAgreement = 0;
        for (InterceptionResult result : results) {
            if (result.getFrameworkPrediction() == result.getIndicatorPrediction()
                    && result.getIndicatorPrediction() != ConceptCategory.NULL) {
                if (result.getRealCategory() != result.getFrameworkPrediction()) {
                    ++badAgreement;
                } else {
                    ++goodAgreement;
                }
            }
        }
        this.setGoodAgreement(goodAgreement);
        this.setGoodDisagreement(badAgreement);


        //Count good and bad abstentions
        int goodAbstention = 0;
        int badAbstention = 0;
        for (InterceptionResult result : results) {
            if (result.getIndicatorPrediction() == ConceptCategory.NULL) {
                if (result.getRealCategory() != result.getFrameworkPrediction()) {
                    ++badAbstention;
                } else {
                    ++goodAbstention;
                }
            }
        }
        this.setGoodAbstention(goodAbstention);
        this.setBadAbstention(badAbstention);


        //Count confusion matrix components
        int indicatorTrueNovelty = 0;
        int indicatorFalseNovelty = 0;
        int indicatorTrueKnown = 0;
        int indicatorFalseKnown = 0;
        int indicatorNotClassifiedKnown = 0;
        int indicatorNotClassifiedNovelty = 0;
        int frameworkTrueNovelty = 0;
        int frameworkFalseNovelty = 0;
        int frameworkTrueKnown = 0;
        int frameworkFalseKnown = 0;
        int frameworkNotClassifiedKnown = 0;
        int frameworkNotClassifiedNovelty = 0;

        for (InterceptionResult result : results) {

            if (result.getRealCategory() == ConceptCategory.NOVELTY) {

                switch (result.getIndicatorPrediction()) {
                    case NOVELTY:
                        ++indicatorTrueNovelty;
                        break;
                    case KNOWN:
                        ++indicatorFalseKnown;
                        break;
                    case NULL:
                        ++indicatorNotClassifiedNovelty;
                        break;
                    default:
                        break;
                }

                switch (result.getFrameworkPrediction()) {
                    case NOVELTY:
                        ++frameworkTrueNovelty;
                        break;
                    case KNOWN:
                        ++frameworkFalseKnown;
                        break;
                    case NULL:
                        ++frameworkNotClassifiedNovelty;
                    default:
                        break;
                }

            } else {

                switch (result.getIndicatorPrediction()) {
                    case NOVELTY:
                        ++indicatorFalseNovelty;
                        break;
                    case KNOWN:
                        ++indicatorTrueKnown;
                        break;
                    case NULL:
                        ++indicatorNotClassifiedKnown;
                        break;
                    default:
                        break;
                }

                switch (result.getFrameworkPrediction()) {
                    case NOVELTY:
                        ++frameworkFalseNovelty;
                        break;
                    case KNOWN:
                        ++frameworkTrueKnown;
                        break;
                    case NULL:
                        ++frameworkNotClassifiedKnown;
                    default:
                        break;
                }

            }
        }

        this.setIndicatorTrueNovelty(indicatorTrueNovelty);
        this.setIndicatorFalseNovelty(indicatorFalseNovelty);
        this.setIndicatorTrueKnown(indicatorTrueKnown);
        this.setIndicatorFalseKnown(indicatorFalseKnown);
        this.setIndicatorNotClassifiedKnown(indicatorNotClassifiedKnown);
        this.setIndicatorNotClassifiedNovelty(indicatorNotClassifiedNovelty);
        this.setFrameworkTrueNovelty(frameworkTrueNovelty);
        this.setFrameworkFalseNovelty(frameworkFalseNovelty);
        this.setFrameworkTrueKnown(frameworkTrueKnown);
        this.setFrameworkFalseKnown(frameworkFalseKnown);
        this.setFrameworkNotClassifiedKnown(frameworkNotClassifiedKnown);
        this.setFrameworkNotClassifiedNovelty(frameworkNotClassifiedNovelty);
    }

    @Override
    public String toString() {
        return "ExperimentResult{" +
                "actualNovelties=" + actualNovelties +
                ", actualKnown=" + actualKnown +
                ", goodDisagreement=" + goodDisagreement +
                ", badDisagreement=" + badDisagreement +
                ", goodAgreement=" + goodAgreement +
                ", badAgreement=" + badAgreement +
                ", goodAbstention=" + goodAbstention +
                ", badAbstention=" + badAbstention +
                ", indicatorTrueNovelty=" + indicatorTrueNovelty +
                ", indicatorFalseNovelty=" + indicatorFalseNovelty +
                ", indicatorTrueKnown=" + indicatorTrueKnown +
                ", indicatorFalseKnown=" + indicatorFalseKnown +
                ", indicatorNotClassifiedKnown=" + indicatorNotClassifiedKnown +
                ", indicatorNotClassifiedNovelty=" + indicatorNotClassifiedNovelty +
                ", frameworkTrueNovelty=" + frameworkTrueNovelty +
                ", frameworkFalseNovelty=" + frameworkFalseNovelty +
                ", frameworkTrueKnown=" + frameworkTrueKnown +
                ", frameworkFalseKnown=" + frameworkFalseKnown +
                ", frameworkNotClassifiedKnown=" + frameworkNotClassifiedKnown +
                ", frameworkNotClassifiedNovelty=" + frameworkNotClassifiedNovelty +
                '}';
    }

    public int getActualNovelties() {
        return actualNovelties;
    }

    public void setActualNovelties(int actualNovelties) {
        this.actualNovelties = actualNovelties;
    }

    public int getActualKnown() {
        return actualKnown;
    }

    public void setActualKnown(int actualKnown) {
        this.actualKnown = actualKnown;
    }

    public int getGoodDisagreement() {
        return goodDisagreement;
    }

    public void setGoodDisagreement(int goodDisagreement) {
        this.goodDisagreement = goodDisagreement;
    }

    public int getBadDisagreement() {
        return badDisagreement;
    }

    public void setBadDisagreement(int badDisagreement) {
        this.badDisagreement = badDisagreement;
    }

    public int getGoodAgreement() {
        return goodAgreement;
    }

    public void setGoodAgreement(int goodAgreement) {
        this.goodAgreement = goodAgreement;
    }

    public int getBadAgreement() {
        return badAgreement;
    }

    public void setBadAgreement(int badAgreement) {
        this.badAgreement = badAgreement;
    }

    public int getGoodAbstention() {
        return goodAbstention;
    }

    public void setGoodAbstention(int goodAbstention) {
        this.goodAbstention = goodAbstention;
    }

    public int getBadAbstention() {
        return badAbstention;
    }

    public void setBadAbstention(int badAbstention) {
        this.badAbstention = badAbstention;
    }

    public int getIndicatorTrueNovelty() {
        return indicatorTrueNovelty;
    }

    public void setIndicatorTrueNovelty(int indicatorTrueNovelty) {
        this.indicatorTrueNovelty = indicatorTrueNovelty;
    }

    public int getIndicatorFalseNovelty() {
        return indicatorFalseNovelty;
    }

    public void setIndicatorFalseNovelty(int indicatorFalseNovelty) {
        this.indicatorFalseNovelty = indicatorFalseNovelty;
    }

    public int getIndicatorTrueKnown() {
        return indicatorTrueKnown;
    }

    public void setIndicatorTrueKnown(int indicatorTrueKnown) {
        this.indicatorTrueKnown = indicatorTrueKnown;
    }

    public int getIndicatorFalseKnown() {
        return indicatorFalseKnown;
    }

    public void setIndicatorFalseKnown(int indicatorFalseKnown) {
        this.indicatorFalseKnown = indicatorFalseKnown;
    }

    public int getIndicatorNotClassifiedKnown() {
        return indicatorNotClassifiedKnown;
    }

    public void setIndicatorNotClassifiedKnown(int indicatorNotClassifiedKnown) {
        this.indicatorNotClassifiedKnown = indicatorNotClassifiedKnown;
    }

    public int getIndicatorNotClassifiedNovelty() {
        return indicatorNotClassifiedNovelty;
    }

    public void setIndicatorNotClassifiedNovelty(int indicatorNotClassifiedNovelty) {
        this.indicatorNotClassifiedNovelty = indicatorNotClassifiedNovelty;
    }

    public int getFrameworkTrueNovelty() {
        return frameworkTrueNovelty;
    }

    public void setFrameworkTrueNovelty(int frameworkTrueNovelty) {
        this.frameworkTrueNovelty = frameworkTrueNovelty;
    }

    public int getFrameworkFalseNovelty() {
        return frameworkFalseNovelty;
    }

    public void setFrameworkFalseNovelty(int frameworkFalseNovelty) {
        this.frameworkFalseNovelty = frameworkFalseNovelty;
    }

    public int getFrameworkTrueKnown() {
        return frameworkTrueKnown;
    }

    public void setFrameworkTrueKnown(int frameworkTrueKnown) {
        this.frameworkTrueKnown = frameworkTrueKnown;
    }

    public int getFrameworkFalseKnown() {
        return frameworkFalseKnown;
    }

    public void setFrameworkFalseKnown(int frameworkFalseKnown) {
        this.frameworkFalseKnown = frameworkFalseKnown;
    }

    public int getFrameworkNotClassifiedKnown() {
        return frameworkNotClassifiedKnown;
    }

    public void setFrameworkNotClassifiedKnown(int frameworkNotClassifiedKnown) {
        this.frameworkNotClassifiedKnown = frameworkNotClassifiedKnown;
    }

    public int getFrameworkNotClassifiedNovelty() {
        return frameworkNotClassifiedNovelty;
    }

    public void setFrameworkNotClassifiedNovelty(int frameworkNotClassifiedNovelty) {
        this.frameworkNotClassifiedNovelty = frameworkNotClassifiedNovelty;
    }
}
