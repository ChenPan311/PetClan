package com.example.android2project.view;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.LocationUtils;
import com.example.android2project.model.MenuAdapter;
import com.example.android2project.model.Post;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.model.ViewPagerAdapter;
import com.example.android2project.view.fragments.AdvertisementFragment;
import com.example.android2project.view.fragments.CommentsFragment;
import com.example.android2project.view.fragments.ConversationFragment;
import com.example.android2project.view.fragments.FeedFragment;
import com.example.android2project.view.fragments.MarketPlaceFragment;
import com.example.android2project.view.fragments.SettingsFragment;
import com.example.android2project.view.fragments.SocialFragment;
import com.example.android2project.view.fragments.UserProfileFragment;
import com.example.android2project.viewmodel.MainViewModel;
import com.example.android2project.viewmodel.UserPictureViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity implements
        FeedFragment.FeedInterface,
        AdvertisementFragment.AdvertisementInterface {

    private DuoDrawerLayout mDrawerLayout;

    private MainViewModel mViewModel;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private UserPictureViewModel mUserPictureViewModel;

    private Observer<Boolean> mSignOutUserObserver;

    private String mCurrentUser;

    private ArrayList<String> mMenuOptions = new ArrayList<>();

    private ViewPager mViewPager;
    private ViewPagerAdapter mPageAdapter;
    private SmoothBottomBar mBottomBar;

    private LocationUtils mLocationUtils;

    private Observer<Address> mOnLocationChanged;

    private TextView userLocationTv;

    private final String COMMENTS_FRAG = "comments_fragment";

    private final int LOCATION_REQUEST_CODE = 1;
    private final int REQUEST_CHECK_SETTINGS_CODE = 2;

    private final String TAG = "MainActivity";

    public interface LocationBuilderDeniedInterface {
        void onLocationDenied(boolean isDenied);
    }

    private LocationBuilderDeniedInterface mLocationListener;

    public void setLocationBuilderDeniedInterface(LocationBuilderDeniedInterface locationBuilderDeniedInterface) {
        this.mLocationListener = locationBuilderDeniedInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getIntent().hasExtra("recipient") || getIntent().hasExtra("post")) {
            onNewIntent(getIntent());
        }

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(this,
                ViewModelEnum.Main)).get(MainViewModel.class);

        mCurrentUser = mViewModel.getUserEmail();

        mUserPictureViewModel = new ViewModelProvider(this, new ViewModelFactory(this,
                ViewModelEnum.Picture)).get(UserPictureViewModel.class);

        mLocationUtils = LocationUtils.getInstance(this);
        registerReceiver(mLocationUtils, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        mOnLocationChanged = new Observer<Address>() {
            @Override
            public void onChanged(Address address) {
                userLocationTv.setText(address.getLocality());
                Log.d(TAG, "onChanged: address: " + address.getLocality());
            }
        };
        mLocationUtils.getLocationLiveData().observe(this, mOnLocationChanged);

        userLocationTv = findViewById(R.id.location_tv);

        final ImageView userProfilePictureIv = findViewById(R.id.user_pic_iv);
        final TextView userNameTv = findViewById(R.id.user_name_tv);
        final Button logOutBtn = findViewById(R.id.log_out_btn);
        mViewPager = findViewById(R.id.view_pager);
        mBottomBar = findViewById(R.id.bottomBar);
        mPageAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getFragments());


        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        final DuoMenuView duoMenuView = (DuoMenuView) findViewById(R.id.menu);
        final MenuAdapter menuAdapter = new MenuAdapter(mMenuOptions);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mBottomBar.setItemActiveIndex(position);
                menuAdapter.setViewSelected(position, true);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setOffscreenPageLimit(2);

        mBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                mViewPager.setCurrentItem(i);
                return false;
            }
        });

        mViewPager.setAdapter(mPageAdapter);

        mSignOutUserObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Intent welcomeIntent = new Intent(MainActivity.this,
                        WelcomeActivity.class);
                startActivity(welcomeIntent);
                finish();
            }
        };

        startObservation();

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.signOutUser();
            }
        });

        setSupportActionBar(toolbar);

        final DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mMenuOptions.add(getResources().getString(R.string.feed));
        mMenuOptions.add(getResources().getString(R.string.chats));
        mMenuOptions.add(getResources().getString(R.string.marketplace));
        mMenuOptions.add(getResources().getString(R.string.profile));
        mMenuOptions.add(getResources().getString(R.string.settings));

        duoMenuView.setOnMenuClickListener(new DuoMenuView.OnMenuClickListener() {
            @Override
            public void onFooterClicked() {
            }

            @Override
            public void onHeaderClicked() {
            }

            @Override
            public void onOptionClicked(int position, Object objectClicked) {
                if (position < 4) {
                    mViewPager.setCurrentItem(position);
                    mDrawerLayout.closeDrawer();
                    menuAdapter.setViewSelected(position, true);
                } else if (position == 4) {
                    if (!mCurrentUser.equals("a@gmail.com")) {
                        mDrawerLayout.closeDrawer();
                        getSupportFragmentManager().beginTransaction()
                                .add(android.R.id.content, SettingsFragment.newInstance())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.prompt_guest_tv), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        duoMenuView.setAdapter(menuAdapter);

        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        userNameTv.setText(mViewModel.getUserName());

        String userProfileImageUri = mViewModel.downloadUserProfilePicture();
        if (userProfileImageUri != null) {
            Log.d(TAG, "URL of downloaded picture: " + userProfileImageUri);
            loadProfilePictureWithGlide(userProfileImageUri, userProfilePictureIv);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getResources().getString(R.string.provide_location), Toast.LENGTH_SHORT).show();
                }

            } else {
                mLocationUtils.startLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS_CODE && resultCode == RESULT_OK) {
            mLocationUtils.startLocation();
            if (mLocationListener != null) {
                mLocationListener.onLocationDenied(false);
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (mLocationListener != null) {
                mLocationListener.onLocationDenied(true);
                Snackbar.make(findViewById(android.R.id.content), R.string.locatio_disabled, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void startObservation() {
        mViewModel.getSignOutSucceed().observe(this, mSignOutUserObserver);
    }

    private void loadProfilePictureWithGlide(String uri, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(MainActivity.this)
                .load(uri)
                .apply(options)
                .into(imageView);
    }

    private List<Fragment> getFragments() {
        fragmentList.add(FeedFragment.newInstance(null));
        fragmentList.add(SocialFragment.newInstance());
        fragmentList.add(MarketPlaceFragment.newInstance());
        fragmentList.add(UserProfileFragment.newInstance(null));

        return fragmentList;
    }


    @Override
    public void onComment(Post post) {
        CommentsFragment.newInstance(post)
                .show(getSupportFragmentManager().beginTransaction(), COMMENTS_FRAG);
    }

    @Override
    public void onAdUploadSucceed(Advertisement ad, AlertDialog loadingDialog, boolean isNewAd) {
        ((MarketPlaceFragment) fragmentList.get(2)).onUploadAdSucceed(ad, loadingDialog, isNewAd);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (action != null && bundle != null) {
            switch (action) {
                case "open_chat":
                    final User recipient = (User) bundle.getSerializable("recipient");
                    if (recipient != null) {
                        ConversationFragment.newInstance(recipient)
                                .show(getSupportFragmentManager()
                                        .beginTransaction(), "fragment_conversation");
                    }
                    break;
                case "open_comments":
                    final Post post = (Post) bundle.getParcelable("post");
                    if (post != null) {
                        CommentsFragment.newInstance(post)
                                .show(getSupportFragmentManager()
                                        .beginTransaction(), COMMENTS_FRAG);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLocationUtils);
    }
}
