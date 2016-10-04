package Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.srh.birthdayassistant.R;

import utils.Resources;
import utils.StringUtils;

public class BadgeImageView extends ImageView{
    private Paint badgeBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint badgeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float badgeSize = 0;
    private String badgeText;
    public BadgeImageView(Context context) {
        super(context);
    }

    public BadgeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public BadgeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs){
        badgeTextPaint.setTextAlign(Paint.Align.CENTER);
        badgeTextPaint.setColor(Color.WHITE);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeImageView, 0, 0);
        try {
            int color = ta.getColor(R.styleable.BadgeImageView_badgeBgColor, Color.TRANSPARENT);
            badgeSize = ta.getDimension(R.styleable.BadgeImageView_badgeSize, 0);
            badgeBgPaint.setColor(color);
            badgeText = ta.getString(R.styleable.BadgeImageView_badgeText);

            badgeTextPaint.setColor(ta.getColor(R.styleable.BadgeImageView_badgeTextColor, Color.WHITE));
            badgeTextPaint.setTextSize(ta.getDimension(R.styleable.BadgeImageView_badgeTextSize, Resources.dp2Px(16)));
        } finally {
            ta.recycle();
        }
    }

    private RectF badgeRect = new RectF();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        badgeRect.set(w - badgeSize, h - badgeSize, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBadge(canvas);
    }

    private void drawBadge(Canvas canvas) {
        if(StringUtils.isEmpty(badgeText)) {
            return;
        }

        canvas.drawRect(badgeRect, badgeBgPaint);

        float xPos = badgeRect.centerX();
        float yPos = (badgeRect.centerY() - ((badgeTextPaint.descent() + badgeTextPaint.ascent()) / 2)) ;
        canvas.drawText(badgeText, xPos, yPos, badgeTextPaint);
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }
}
