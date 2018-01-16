package workshop.akbolatss.xchangesrates.screens.about;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;
import workshop.akbolatss.xchangesrates.R;

public class AboutFragment extends SupportFragment {

    @BindView(R.id.flAbout)
    protected FrameLayout mFrameL;

    @BindView(R.id.progressBar)
    protected ProgressBar mProgress;

    private AboutView aboutView;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aboutView = AboutBuilder.with(view.getContext())
                        .setCover(R.mipmap.profile_cover)
                        .setName(getString(R.string.my_name))
                        .setPhoto(R.mipmap.profile_picture)
                        .setSubTitle(R.string.profile_subtitle)
                        .setBrief(getString(R.string.profile_brief))
                        .setAppIcon(R.mipmap.ic_launcher)
                        .setAppName(R.string.app_name)

                        .addGooglePlayStoreLink(getString(R.string.profile_playstore_link))
                        .addGitHubLink(getString(R.string.profile_github))
                        .addInstagramLink(getString(R.string.profile_instagram))
                        .addFeedbackAction(getString(R.string.profile_feedback))
                        .addMoreFromMeAction(getString(R.string.profile_more_from_me))

                        .addFiveStarsAction()
//                .addIntroduceAction((Intent) null)
//                .addHelpAction((Intent) null)
//                .addChangeLogAction((Intent) null)
//                .addRemoveAdsAction((Intent) null)
//                .addDonateAction((Intent) null)

                        .addShareAction(R.string.app_name)
                        .setWrapScrollView(true)
                        .setLinksAnimated(true)
                        .setShowAsCard(true)
                        .build();
                mFrameL.addView(aboutView);
                mProgress.setVisibility(View.GONE);
                mFrameL.setVisibility(View.VISIBLE);
            }
        }, 750);
    }
}
