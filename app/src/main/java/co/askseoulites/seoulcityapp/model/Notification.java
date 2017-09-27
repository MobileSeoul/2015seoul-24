/**
 * Copyright 2014 Facebook, Inc.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */

package co.askseoulites.seoulcityapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@ParseClassName("Notification")
public class Notification extends ParseObject {

	public String getTitle() {
		return getString("title");
	}
    
    public void setTitle(String title) { put("title", title);}

	public String getContent() {
		return getString("content");
	}
    public void setContent(String content) { put("content", content);}
    
	public String getUrl() {
		return getString("url");
	}
    public void setUrl(String url) { put("url", url); }

    public String getPostId() {
        return getString("postId");
    }
    public String getPayload() {
        return  getString("payload");
    }
    
	public boolean isLink() {
		return getBoolean("isLink");
	}
    public void setisLink(boolean isLink) { put("isLink", isLink);}


    public static String convertDateToString(Date date) {

        if(date == null)
            return new SimpleDateFormat("MMM d").format(new Date());
        Format formatter = new SimpleDateFormat("MMM d" );
        String s = formatter.format(date);

        return s;

    }


}
