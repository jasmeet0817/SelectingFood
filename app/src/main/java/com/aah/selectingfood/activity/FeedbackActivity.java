package com.aah.selectingfood.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aah.selectingfood.adapter.FeedbackViewPagerAdapter;
import com.aah.selectingfood.R;
import com.aah.selectingfood.model.Child;
import com.aah.selectingfood.helper.DataManagement;
import com.aah.selectingfood.model.FeedbackCard;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private LinearLayout pagerIndicator;
    private int dotsCount;
    private ImageView[] dots;
    private FeedbackViewPagerAdapter pagerAdapter;
    ViewPager viewPager;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        viewPager = (ViewPager) findViewById(R.id.pager_introduction);
        pagerIndicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        pagerAdapter = new FeedbackViewPagerAdapter(FeedbackActivity.this, getFeedbackCards());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setPageMargin(100);
        viewPager.addOnPageChangeListener(this);
        setUiPageViewController();

        //Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        setPermissions();
    }

    public void ShareFeedbackCard(View v){
        final int position = viewPager.getCurrentItem();
        View view = viewPager.getChildAt(position);
        Bitmap image = getBitmapFromView(view);
        shareGeneral(image);
    }

    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }


    private List<FeedbackCard> getFeedbackCards() {
        List<FeedbackCard> feedbackCards = new ArrayList<>();

        // Create all different feedback cards

        // Create summary feedback for children 1-3
        // TODO: Make sure the summary feedback is correct for 1-3 children.
        FeedbackCard finalFoodSummaryFeedback = DataManagement.getInstance(this).getUser().getChildren().get(0).giveFeedbackFinalFoodSummary(DataManagement.getInstance(this).getSelectedFood());
        finalFoodSummaryFeedback.setImage(DataManagement.getInstance(this).loadBitmapFromAssets("balance.png", "feedbackImages"));
        feedbackCards.add(finalFoodSummaryFeedback);

        // Create individual food feedback for children 1-3
        // TODO: Analyze each food.

        // Create general feedback for children 1-3
        for (Child child : DataManagement.getInstance(this).getUser().getChildren()) {
            FeedbackCard generalAgeFeedback = child.giveFeedbackFinalGeneral();
            generalAgeFeedback.setImage(DataManagement.getInstance(this).loadBitmapFromAssets("childHappyDefault.png", "childrenImages"));
            feedbackCards.add(generalAgeFeedback);
        }
        return feedbackCards;
    }


    private void setUiPageViewController() {
        dotsCount = pagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.dot_nonselecteditem));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(20, 0, 20, 0);

            pagerIndicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.dot_selecteditem));
    }

    private void shareOnFacebook(Bitmap image) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("testesteestetetstestsetse")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareDialog.show(content);
    }

    private void setPermissions(){
        int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 311390813;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique

                return;
            }
        }
    }

    private void shareGeneral(Bitmap imageBitmap) {
        setPermissions();

        try {
            PackageManager packageManager = getPackageManager();
            int hasPerm = packageManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "sharepicture", null);
                Uri bmpUri = Uri.parse(pathofBmp);
                final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
            } else Toast.makeText(this, R.string.writeext_permission_error, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, R.string.writeext_permission_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.dot_nonselecteditem));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.dot_selecteditem));
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restart_menu, menu);
        return true;
    }

}
