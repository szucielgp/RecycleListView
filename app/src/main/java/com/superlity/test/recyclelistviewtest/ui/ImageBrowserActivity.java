package com.superlity.test.recyclelistviewtest.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.utils.PhotoUtils;

/**
 * Created by lzw on 14-9-21.
 */
public class ImageBrowserActivity extends AppCompatActivity {
  private static final String PATH = "path";
  private static final String URL = "url";
  private ImageView imageView;

  public static void go(Context ctx, String path, String url) {
    Intent intent = new Intent(ctx, ImageBrowserActivity.class);
    intent.putExtra(PATH, path);
    intent.putExtra(URL, url);
    ctx.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chat_image_brower_layout);
    imageView = (ImageView) findViewById(R.id.imageView);
    Intent intent = getIntent();
    String path = intent.getStringExtra(PATH);
    String url = intent.getStringExtra(URL);
    PhotoUtils.displayImageCacheElseNetwork(imageView, path, url);
  }
}
