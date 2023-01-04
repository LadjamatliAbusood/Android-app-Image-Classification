    package com.example.lanchanika;

    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.LinearLayout;

    import androidx.annotation.NonNull;
    import androidx.viewpager.widget.PagerAdapter;

    public class ViewPagerAdapter extends PagerAdapter {
        Context context;

        int images[] = {

                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five

        };

        public ViewPagerAdapter(Context context){
            this.context = context;
        }
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (LinearLayout) object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.slider_layout,container, false);

            ImageView slidetitleimage = (ImageView) view.findViewById(R.id.titleImage);
            slidetitleimage.setImageResource(images[position]);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout)object);
        }
    }