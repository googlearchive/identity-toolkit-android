/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.identitytoolkit.demo;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Helper class for sending and receiving HTTP request and response.
 */
public final class HttpUtils {

  private HttpUtils() {}

  public static byte[] get(String url) throws IOException {
    Log.i("HttpUtils", "HTTP GET " + url);
    return request("GET", url, null);
  }

  public static byte[] post(String url, byte[] body) throws IOException {
    Log.i("HttpUtils", "HTTP POST " + url);
    return request("POST", url, body);
  }

  private static byte[] request(String method, String url, byte[] body) throws IOException {
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setDoInput(true);
      if (method.equals("POST")) {
        conn.setDoOutput(true);
        conn.setFixedLengthStreamingMode(body.length);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");
        OutputStream os = conn.getOutputStream();
        os.write(body);
        os.close();
      }

      BufferedInputStream in;
      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        in = new BufferedInputStream(conn.getInputStream());
      } else {
        in = new BufferedInputStream(conn.getErrorStream());
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
      in.close();
      return out.toByteArray();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }
}
