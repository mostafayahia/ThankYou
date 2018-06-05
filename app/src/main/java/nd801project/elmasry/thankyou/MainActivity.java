package nd801project.elmasry.thankyou;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout = findViewById(R.id.tabs);

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_all));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_fav));

    }
}
