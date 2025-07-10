package alberapps.java.tram.news;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

public class ParserXML {

    private final String ns = null;


    public Noticias parserNoticias(String urlEntrada, Boolean usarCache, String userAgentDefault, Context context) {

        List<NoticiaRss> listaNoticias = new ArrayList<NoticiaRss>();

        InputStream st = null;

        Noticias noticias = null;

        try {

            String conexion = null;
            if(urlEntrada.contains("tramalacant.es")) {
                conexion = Conectividad.conexionGetWebBusCert(urlEntrada, usarCache, userAgentDefault, true, context);
            } else {
                conexion = Conectividad.conexionGetUtf8StringUserAgent(urlEntrada, usarCache, userAgentDefault);
            }

            st = Utilidades.stringToStream(conexion);

            noticias = parse(st);


        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(e);
        } finally {

            try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException eb) {

            }

        }

        return noticias;
    }


    /**
     * @param in
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public Noticias parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag();

            return readChannel(parser);
        } finally {
            in.close();
        }
    }


    /**
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Noticias readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
        Noticias noticias = new Noticias();
        noticias.setNoticiasList(new ArrayList<NoticiaRss>());

        parser.require(XmlPullParser.START_TAG, ns, "channel");


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("item")) {
                noticias.getNoticiasList().add(readItem(parser));
            } else if (name.equals("title")) {
                noticias.setTitle(readText(parser));
            } else if (name.equals("description") || name.equals("atom:summary")) {
                noticias.setDescription(readText(parser));
            } else if (name.equals("image")) {
                noticias.setImage(readImage(parser));
            }else {
                skip(parser);
            }
        }
        return noticias;
    }


    /**
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private NoticiaRss readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        NoticiaRss noticia = new NoticiaRss();

        String[] enclosureAux = null;

        parser.require(XmlPullParser.START_TAG, ns, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the item tag
            if (name.equals("title")) {
                noticia.setTitle(readText(parser));
            } else if (name.equals("description") || name.equals("atom:summary")) {
                noticia.setDescription(readText(parser));
            } else if (name.equals("link")) {
                noticia.setLink((readText(parser)));
            } else if (name.equals("comments")) {
                noticia.setComments((readText(parser)));
            } else if (name.equals("pubDate")) {
                noticia.setPubDate((readText(parser)));
            } else if (name.equals("content:encoded")) {
                noticia.setContentEncoded((readText(parser)));
            } else if (name.equals("wfw:commentRss")) {
                noticia.setCommentRss((readText(parser)));
            } else if (name.equals("slash:comments")) {
                noticia.setLink((readText(parser)));
            } else if (name.equals("guid")) {
                noticia.setGuid((readText(parser)));
            } else if (name.equals("author")) {
                noticia.setAuthor((readText(parser)));
            } else if (name.equals("itunes:duration")) {
                noticia.setDuration((readText(parser)));
            } else if (name.equals("itunes:author")) {
                noticia.setAuthorShort((readText(parser)));
            } else if (name.equals("enclosure")) {
                enclosureAux = readEnclosure(parser);
                noticia.setEnclosureUrl(enclosureAux[0]);
                //noticia.setDuration(enclosureAux[1]);
            }

            else {
                skip(parser);
            }
        }
        return noticia;
    }

    private String readImage(XmlPullParser parser) throws XmlPullParserException, IOException {

        String url = "";

        parser.require(XmlPullParser.START_TAG, ns, "image");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the item tag
            if (name.equals("url")) {
                url = readText(parser);
            }

            else {
                skip(parser);
            }
        }
        return url;
    }


    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // For the tags title and summary, extracts their text values.
    private String[] readEnclosure(XmlPullParser parser) throws IOException, XmlPullParserException {
        String[] result = {"",""};
        result[0] = parser.getAttributeValue(0);
        result[1] = parser.getAttributeValue(1);

        if (parser.next() == XmlPullParser.TEXT) {
            //result = parser.getText();
            parser.nextTag();
        }

        return result;
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
