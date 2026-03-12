package com.example.kunworld.ui.istikhara;

import com.example.kunworld.R;
import com.example.kunworld.utils.AbjadCalculator;

public class MarriageMatchFragment extends BaseIstikharaTabFragment {

    private NameInputHolder personAInput;
    private NameInputHolder personAMotherInput;
    private NameInputHolder personBInput;
    private NameInputHolder personBMotherInput;

    @Override
    protected int getTitleRes() {
        return R.string.istikhara_marriage_title;
    }

    @Override
    protected int getFeaturedImageRes() {
        return R.drawable.istikhara_marriage;
    }

    @Override
    protected void setupInputFields() {
        personAInput = addNameInput(getString(R.string.istikhara_person_a));
        personAMotherInput = addNameInput(getString(R.string.istikhara_person_a_mother));
        personBInput = addNameInput(getString(R.string.istikhara_person_b));
        personBMotherInput = addNameInput(getString(R.string.istikhara_person_b_mother));
    }

    @Override
    protected void onCalculate() {
        if (!validateInputs(personAInput, personAMotherInput, personBInput, personBMotherInput)) {
            showError(getString(R.string.error_four_names_required));
            return;
        }

        AbjadCalculator.MarriageResult result = AbjadCalculator.calculateMarriageMatch(
                personAInput.urduValue,
                personAMotherInput.urduValue,
                personBInput.urduValue,
                personBMotherInput.urduValue
        );

        String mainResult = result.successRatio + "%";
        String details = getString(R.string.result_success_ratio) + "\n" + result.status;

        showResult(
                getString(R.string.result_marriage_match),
                mainResult,
                details
        );
    }
}
