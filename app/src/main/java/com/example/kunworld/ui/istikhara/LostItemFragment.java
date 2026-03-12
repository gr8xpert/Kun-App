package com.example.kunworld.ui.istikhara;

import com.example.kunworld.R;
import com.example.kunworld.utils.AbjadCalculator;

public class LostItemFragment extends BaseIstikharaTabFragment {

    private NameInputHolder personInput;

    @Override
    protected int getTitleRes() {
        return R.string.istikhara_lost_title;
    }

    @Override
    protected int getFeaturedImageRes() {
        return R.drawable.istikhara_lost;
    }

    @Override
    protected void setupInputFields() {
        personInput = addNameInput(getString(R.string.istikhara_person_name));
    }

    @Override
    protected void onCalculate() {
        if (!validateInputs(personInput)) {
            showError(getString(R.string.error_name_required));
            return;
        }

        AbjadCalculator.LostItemResult result = AbjadCalculator.calculateLostItem(
                personInput.urduValue
        );

        String details = String.format("Today: %s (%s)",
                result.todayEnglish,
                result.todayUrdu);

        showResult(
                getString(R.string.result_lost_item),
                result.message,
                details
        );
    }
}
