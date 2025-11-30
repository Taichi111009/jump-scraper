import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JumpScraper {

    public static void main(String[] args) {
        // ターゲットURL
        String url = "https://shonenjumpplus.com/episode/17106567254627463963";

        // ヘッダー設定（あなたが取得した情報をそのまま適用）
        Map<String, String> headers = new HashMap<>();
        headers.put("authority", "shonenjumpplus.com");
        headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8");
        headers.put("accept-language", "ja;q=0.9");
        headers.put("cache-control", "max-age=0");
        
        // 【重要】Cookieの鮮度に注意
        headers.put("cookie", "glsc=dGeGLe5Mra4aVz5UpVxkPqLoYIGuc2cFSx5hlhDwFJiEUCYqjZnKvVUS8XuiFa14");
        
        headers.put("referer", "https://shonenjumpplus.com/episode/17106567256884586860");
        headers.put("sec-ch-ua", "\"Chromium\";v=\"142\", \"Brave\";v=\"142\", \"Not_A Brand\";v=\"99\"");
        headers.put("sec-ch-ua-mobile", "?1");
        headers.put("sec-ch-ua-platform", "\"Android\"");
        headers.put("sec-fetch-dest", "document");
        headers.put("sec-fetch-mode", "navigate");
        headers.put("sec-fetch-site", "same-origin");
        headers.put("sec-fetch-user", "?1");
        headers.put("upgrade-insecure-requests", "1");
        headers.put("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Mobile Safari/537.36");

        try {
            // 1. 接続と取得
            // Jsoupはgzip圧縮などを自動でハンドリングするため、Accept-Encodingは指定しない方が無難です
            Connection.Response response = Jsoup.connect(url)
                    .headers(headers)
                    .method(Connection.Method.GET)
                    .ignoreHttpErrors(true) // エラーでも一旦HTMLを見る設定
                    .execute();

            // ステータスコードの確認
            if (response.statusCode() != 200) {
                System.err.println("Error: Status Code " + response.statusCode());
                // デバッグ用にレスポンスの一部を表示
                System.err.println(response.body().substring(0, Math.min(response.body().length(), 500)));
                return;
            }

            Document doc = response.parse();

            // 2. 要素の抽出
            // id="episode-json" を持つ script タグを探す
            Element scriptElement = doc.selectFirst("script#episode-json");

            if (scriptElement != null) {
                // 3. データの抽出と変換
                // .attr("data-value") を使うだけで、&quot; などは自動的にデコードされます
                String json = scriptElement.attr("data-value");
                
                if (json.isEmpty()) {
                     System.err.println("タグは見つかりましたが、data-valueが空です。");
                } else {
                    // 標準出力へ吐き出し
                    System.out.println(json);
                }
            } else {
                System.err.println("ターゲットのタグ <script id='episode-json'> が見つかりませんでした。");
                // どんなHTMLが返ってきたか確認したい場合は以下を有効化
                // System.err.println(doc.html());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
