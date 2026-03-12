package com.example.kunworld.ui.istikhara;

import com.example.kunworld.R;
import com.example.kunworld.utils.AbjadCalculator;

public class PersonalityMatchFragment extends BaseIstikharaTabFragment {

    private NameInputHolder personAInput;
    private NameInputHolder personBInput;

    @Override
    protected int getTitleRes() {
        return R.string.istikhara_personality_title;
    }

    @Override
    protected int getFeaturedImageRes() {
        return R.drawable.istikhara_personality;
    }

    @Override
    protected void setupInputFields() {
        personAInput = addNameInput(getString(R.string.istikhara_person_a));
        personBInput = addNameInput(getString(R.string.istikhara_person_b));
    }

    @Override
    protected void onCalculate() {
        if (!validateInputs(personAInput, personBInput)) {
            showError(getString(R.string.error_both_names_required));
            return;
        }

        AbjadCalculator.PersonalityResult result = AbjadCalculator.calculatePersonalityMatch(
                personAInput.urduValue,
                personBInput.urduValue
        );

        if (result.totalA == 0 || result.totalB == 0) {
            showError(getString(R.string.error_no_valid_letters));
            return;
        }

        String details = String.format("%s: %d | %s: %d",
                personAInput.urduValue, result.totalA,
                personBInput.urduValue, result.totalB);

        showResult(
                getString(R.string.result_personality_match),
                result.personalityType,
                details
        );
    }
}
