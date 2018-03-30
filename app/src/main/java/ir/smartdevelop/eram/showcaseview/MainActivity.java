package ir.smartdevelop.eram.showcaseview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Toast;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;

public class MainActivity extends AppCompatActivity {

    private GuideView mGuideView;
    private GuideView.Builder builder;
    private GuideView.Builder builder2;
    private GuideView.Builder builder3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AppCompatImageView imageView = findViewById(R.id.imageView);
        final View view1 = findViewById(R.id.view1);
        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);
        final View view4 = findViewById(R.id.view4);
        final View view5 = findViewById(R.id.view5);

        builder2 = new GuideView.Builder(this)
                .setTitle("Some title")
                .setContentText("this is something")
                .setGravity(GuideView.Gravity.left)
                .setDismissType(GuideView.DismissType.outside)
                .setTargetView(view1)
                .setButtonText("OK, GOT IT")
                .setBtnDrawable(R.drawable.green_btn)
                .setGuideListener(new GuideView.GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.view1:
                                builder3.setTargetView(view3).build();
                                break;
                        }
                        mGuideView = builder3.build();
                        mGuideView.show();
                    }
                });

        builder = new GuideView.Builder(MainActivity.this)
                .setTitle("Guide Title Text")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setGravity(GuideView.Gravity.right)
                .setDismissType(GuideView.DismissType.outside)
                .setButtonText("OK GOT IT")
                .setWindowShape(GuideView.WindowShape.circle)
                .setBtnDrawable(R.drawable.green_btn)
                .setTargetView(imageView)
                .setGuideListener(new GuideView.GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.imageView:
                                builder2.setTargetView(view1).build();
                                break;
                        }
                        mGuideView = builder2.build();
                        mGuideView.show();
                    }
                });

        builder3 = new GuideView.Builder(MainActivity.this)
                .setTitle("Centered Text")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setGravity(GuideView.Gravity.center)
                .setDismissType(GuideView.DismissType.outside)
                .setButtonText("OK GOT IT")
                .setBtnDrawable(R.drawable.green_btn)
                .setTargetView(view3)
                .setGuideListener(new GuideView.GuideListener() {
                    @Override
                    public void onDismiss(View view) {
//                        switch (view.getId()) {
//                            case R.id.imageView:
//                                builder2.setTargetView(view1).build();
//                                break;
//                        }
//                        mGuideView = builder2.build();
//                        mGuideView.show();
                    }
                });


        mGuideView = builder.build();
        mGuideView.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
