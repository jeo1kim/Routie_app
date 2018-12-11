package com.example.android.effectivenavigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ViewSwitcher;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ui.ParseLoginBuilder;


public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseFacebookUtils.initialize(this);
        ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        //if(AccessToken.getCurrentAccessToken()==null) {
            ParseLoginBuilder builder = new ParseLoginBuilder(Main2Activity.this);
            startActivityForResult(builder.build(), 0);
        //}

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setLogo(R.drawable.routie_icon);
//        setSupportActionBar(toolbar);

                /*hide keyboard======================================*/
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("List"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.addTab(tabLayout.newTab().setText("Option"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), viewPager);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.notifyDataSetChanged();
                View focus = getCurrentFocus();
                if (focus != null) {
                    hiddenKeyboard(focus);
                }
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View focus = getCurrentFocus();
                if (focus != null) {
                    hiddenKeyboard(focus);
                }
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                View focus = getCurrentFocus();
                if (focus != null) {
                    hiddenKeyboard(focus);
                }
                //adapter.notifyDataSetChanged();
            }
        });
    }

    //hide keyboarddddddddddddddddddd
    private void hiddenKeyboard(View v) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class TinderSectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.layout_testing, container, false);
//            //hide keyboard============================================
//            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//            inputManager.hideSoftInputFromWindow(rootView.getWindowToken(),0);

            rootView.findViewById(R.id.imgBtn_cafes)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "cafes");
                            startActivity(intent);

                        }
                    });
            rootView.findViewById(R.id.imgBtn_food)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "food");
                            startActivity(intent);
                        }
                    });
            rootView.findViewById(R.id.imgBtn_shops)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "shopping");
                            startActivity(intent);
                        }
                    });
            rootView.findViewById(R.id.imgBtn_entertainment)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "arts");
                            startActivity(intent);
                        }
                    });
            rootView.findViewById(R.id.imgBtn_night)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "nightlife");
                            startActivity(intent);
                        }
                    });
            rootView.findViewById(R.id.imgBtn_sights)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "landmarks");
                            startActivity(intent);
                        }
                    });
            rootView.findViewById(R.id.imgBtn_following)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "beautysvc");
                            startActivity(intent);
                        }
                    });

            rootView.findViewById(R.id.imgBtn_outdoor)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "active");
                            startActivity(intent);
                        }
                    });

            rootView.findViewById(R.id.imgBtn_surpriseme)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), TinderUI.class);
                            intent.putExtra("category", "adultentertainment");
                            startActivity(intent);
                        }
                    });
            return rootView;
        }
    }

    public static class Option extends Fragment{

        private int viewNum = 1;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.settings,container,false);
            final ViewSwitcher viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.settings_switcher);




            if(viewNum == 2)
                viewSwitcher.showNext();



            rootView.findViewById(R.id.saving_button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewSwitcher.showNext();

                    viewNum = 2;
                }
            });

            rootView.findViewById(R.id.booking_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewSwitcher.showPrevious();
                    viewNum = 1;
                }
            });
            return rootView;
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onResume() {
            super.onResume();
        }
        @Override
        public void onPause() {
            super.onPause();
        }
        @Override
        public void onAttach(Activity activity)
        {
            super.onAttach(activity);
        }


        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

        }
    }

}

