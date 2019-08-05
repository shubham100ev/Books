package com.example.books;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private String BOOK_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private BookDetailAdapter mAdapter;

    public static final String LOG_TAG = MainActivity.class.getName();

    String topic[] = new String[]{"Android", "Web", "Games"};
    AutoCompleteTextView autoCompleteTextView;
    Button search;

    ProgressBar progressBar;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoCompleteTextView = findViewById(R.id.autocomptext);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, topic);
        autoCompleteTextView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);

        search = findViewById(R.id.btn);

        final ListView book = findViewById(R.id.listView);

        mAdapter = new BookDetailAdapter(this, new ArrayList<BookDetail>());


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear();
                String BOOK_URL_ADD = autoCompleteTextView.getText() + "&maxResults=20";
                BookAsyncTask task = new BookAsyncTask();
                task.execute(BOOK_URL + BOOK_URL_ADD);
                search.setEnabled(false);
                book.setAdapter(mAdapter);
                progressBar.setVisibility(View.VISIBLE);
                autoCompleteTextView.setEnabled(false);
//                countDownTimer=new CountDownTimer(15000,1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        progressBar.setVisibility(View.INVISIBLE);
//                        search.setEnabled(true);
//                        if(mAdapter.getCount()==0)
//                            Toast.makeText(MainActivity.this,"No Book Found",Toast.LENGTH_SHORT).show();
//                    }
//                }.start();
            }
        });


        book.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookDetail bookDetail = mAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(bookDetail.getPreviewLink()));
                startActivity(intent);
            }
        });

    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<BookDetail>> {

        @Override
        protected List<BookDetail> doInBackground(String... strings) {
            URL url = createUrl(strings[0]);


            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return extractFeatureFromJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(List<BookDetail> bookDetails) {
            mAdapter.clear();
            if (bookDetails != null && !bookDetails.isEmpty()) {
                mAdapter.addAll(bookDetails);
                search.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                autoCompleteTextView.setEnabled(true);
            }
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null)
                return jsonResponse;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("Status code", "Status code not equal to 200");
                    progressBar.setVisibility(View.INVISIBLE);
                    search.setEnabled(true);
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem in connection", e);
                progressBar.setVisibility(View.INVISIBLE);
                search.setEnabled(true);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }

            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<BookDetail> extractFeatureFromJson(String bookJSON) {
            if (TextUtils.isEmpty(bookJSON))
                return null;

            List<BookDetail> booklist = new ArrayList<>();

            try {
                JSONObject baseJSON = new JSONObject(bookJSON);
                JSONArray bookArray = baseJSON.getJSONArray("items");
                for (int i = 0; i < bookArray.length(); i++) {
                    JSONObject currentBook = bookArray.getJSONObject(i);

                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    String title = volumeInfo.getString("title");

                    String author = volumeInfo.getString("authors");

                    String previewLink = volumeInfo.getString("previewLink");

                    JSONObject imageLink =volumeInfo.getJSONObject("imageLinks");

                    String thumbnail=imageLink.getString("thumbnail");

                    thumbnail=thumbnail.replace("http","https");

                    BookDetail bookDetail = new BookDetail(title, author, previewLink,thumbnail);

                    booklist.add(bookDetail);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return booklist;
        }

    }
}
