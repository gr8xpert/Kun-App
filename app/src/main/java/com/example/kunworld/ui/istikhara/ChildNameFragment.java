package com.example.kunworld.ui.istikhara;

import com.example.kunworld.R;
import com.example.kunworld.utils.AbjadCalculator;

public class ChildNameFragment extends BaseIstikharaTabFragment {

    private NameInputHolder childInput;
    private NameInputHolder fatherInput;
    private NameInputHolder motherInput;

    @Override
    protected int getTitleRes() {
        return R.string.istikhara_child_title;
    }

    @Override
    protected int getFeaturedImageRes() {
        return R.drawable.istikhara_child;
    }

    @Override
    protected void setupInputFields() {
        childInput = addNameInput(getString(R.string.istikhara_child_name));
        fatherInput = addNameInput(getString(R.string.istikhara_father_name));
        motherInput = addNameInput(getString(R.string.istikhara_mother_name));
    }

    @Override
    protected void onCalculate() {
        if (!validateInputs(childInput, fatherInput, motherInput)) {
            showError(getString(R.string.error_three_names_required));
            return;
        }

        AbjadCalculator.ChildNameResult result = AbjadCalculator.calculateChildName(
                childInput.urduValue,
                fatherInput.urduValue,
                motherInput.urduValue
        );

        if (result.totalChild == 0 || result.totalFather == 0 || result.totalMother == 0) {
            showError(getString(R.string.error_no_valid_letters));
            return;
        }

        String mainResult = result.finalPercentage + "%";
        String details = String.format("With Father: %s\nWith Mother: %s",
                result.relationshipFather,
                result.relationshipMother);

        showResult(
                getString(R.string.result_name_recommendation),
                mainResult,
                details
        );
    }
}
