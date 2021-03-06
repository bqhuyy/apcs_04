package cs300.apcs04.traveltogether;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class PlaceViewHolder extends GroupViewHolder{

	private TextView mName;
	private RatingBar mRating;
	private ImageView mArrow;
	public Boolean isExpand = false;

	public ImageView getmArrow(){
		return this.mArrow;
	}

	public PlaceViewHolder(View itemView) {
		super(itemView);
		mName = itemView.findViewById(R.id.PlaceItem_place_name);
		mRating = itemView.findViewById(R.id.PlaceItem_place_rating);
		//mArrow = itemView.findViewById(R.id.PlaceItem_arrow_expand);

	}

	public void setPlaceName(ExpandableGroup place){
		//Place p = (Place) place;
		Place p = null;
		mName.setText(place.getTitle());
		mRating.setRating(p.getmRating());
	}

	@Override
	public void expand() {
		animateExpand();
		isExpand = true;
	}

	@Override
	public void collapse() {
		animateCollapse();
		isExpand = false;
	}

	public void animateExpand(){
		RotateAnimation rotate =
				new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(300);
		rotate.setFillAfter(true);
		mArrow.setAnimation(rotate);
	}

	public void animateCollapse(){
		RotateAnimation rotate =
				new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(300);
		rotate.setFillAfter(true);
		mArrow.setAnimation(rotate);
	}

}
