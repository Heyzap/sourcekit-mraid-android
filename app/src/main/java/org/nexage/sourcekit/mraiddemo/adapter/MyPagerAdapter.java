package org.nexage.sourcekit.mraiddemo.adapter;

import org.nexage.sourcekit.mraiddemo.AboutFragment;
import org.nexage.sourcekit.mraiddemo.BannerFragment;
import org.nexage.sourcekit.mraiddemo.InterstitialFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

	public MyPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return new BannerFragment();
		case 1:
			return new InterstitialFragment();
		case 2:
			return new AboutFragment();
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
