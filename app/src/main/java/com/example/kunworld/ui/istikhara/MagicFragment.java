package com.example.kunworld.ui.istikhara;

import com.example.kunworld.R;
import com.example.kunworld.utils.AbjadCalculator;

public class MagicFragment extends BaseIstikharaTabFragment {

    private NameInputHolder personInput;
    private NameInputHolder motherInput;

    @Override
    protected int getTitleRes() {
        return R.string.istikhara_magic_title;
    }

    @Override
    protected int getFeaturedImageRes() {
        return R.drawable.istikhara_magic;
    }

    @Override
    protected void setupInputFields() {
        personInput = addNameInput(getString(R.string.istikhara_person_name));
        motherInput = addNameInput(getString(R.string.istikhara_mother_name));
    }

    @Override
    protected void onCalculate() {
        if (!validateInputs(personInput, motherInput)) {
            showError(getString(R.string.error_both_names_required));
            return;
        }

        AbjadCalculator.MagicResult result = AbjadCalculator.calculateMagic(
                personInput.urduValue,
                motherInput.urduValue
        );

        String details = String.format("Today: %s (%s)\nDay Value: %d",
                result.todayEnglish,
                result.todayUrdu,
                result.dayTotal);

        showResult(
                getString(R.string.result_answer),
                result.answer,
                details
        );
    }
}
