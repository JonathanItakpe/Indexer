/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.*;
import java.util.*;
import java.util.Arrays.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import static java.util.stream.StreamSupport.stream;
import javax.swing.JOptionPane;
import javax.ws.rs.core.Response;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 *
 * @author PR0PH3T
 */
public class Indexer {
// Initialize the tagger

    public static MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
    //Stop words
    public static String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
    public static Set<String> stopWordsSet = new HashSet<>(Arrays.asList(stopwords)); //Converting stopwords array to Set
    public static File temp_pdf;

    /**
     * @param filePath
     * @return
     */
    // KIND OF USELESS
    public static String getText(String filePath) {
        File file;
        String fd = null;
        XWPFWordExtractor extractor;
        try {
            file = new File(filePath);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            XWPFDocument document = new XWPFDocument(fis);
            extractor = new XWPFWordExtractor(document);
            fd = extractor.getText();
        } catch (IOException exep) {
            exep.printStackTrace();
        }
        return fd;
    }

    //COnverting DOCX to PDF
    private static String createPDFfromDocx(String file_loc) {
        try {
            //Create Temporary File
            temp_pdf = File.createTempFile("tempPDF", ".pdf");
            temp_pdf.deleteOnExit();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            long start = System.currentTimeMillis();

            // 1) Load DOCX into WordprocessingMLPackage
            InputStream is = new FileInputStream(new File(file_loc));
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);

            // 2) Prepare Pdf settings
            PdfSettings pdfSettings = new PdfSettings();

            // 3) Convert WordprocessingMLPackage to Pdf
            OutputStream out = new FileOutputStream(temp_pdf);
            PdfConversion converter = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(wordMLPackage);
            converter.output(out, pdfSettings);

            System.err.println("Generate pdf with "
                    + (System.currentTimeMillis() - start) + "ms");
                      
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        return temp_pdf.getAbsolutePath();
    }

    //GET NUMBER OF PAGES IN PDF FILE
    public static int getPDFPages(String fileLoc) throws IOException {
        PDDocument doc = PDDocument.load(new File(fileLoc));
        int count = doc.getNumberOfPages();
        return count;
    }

    //GETTING PDF STRING USING PDFBOX
    public static String getPDF(String fileLoc, int pageNumber) {
        PDDocument pdf = null;
        String parsedText = null;
        COSDocument cosDoc = null;
        //BufferedWriter br = null;
        try {
            File inputPDF = new File(fileLoc);
            PDFParser parser = new PDFParser(new FileInputStream(inputPDF));
            parser.parse();
            cosDoc = parser.getDocument();
            pdf = new PDDocument(cosDoc);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            //br = new BufferedWriter( new OutputStreamWriter(null));
            //stripper.writeText(pdf, br);
            parsedText = stripper.getText(pdf);
            pdf.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return parsedText;
    }
    
    //REMOVING URLS
    public static String removeUrl(String text) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        int i = 0;
        while (m.find()) {
            text = text.replaceAll(m.group(i), "").trim();
            i++;
        }
        return text;
    }
    
    //CREATING ARRAY OF WORDS
    public static ArrayList<String> createArray(String file_data, String[] notimpo_words) {
        ArrayList<String> wordsList = new ArrayList<>();

        //get set of not important words of it is NOT empty
        if (notimpo_words != null) {
            // COnvert Ingnored words to Set  
            Set<String> _ignored = new HashSet<>(Arrays.asList(notimpo_words));
            stopWordsSet.addAll(_ignored); //add to be ignored words to the stopwords
        }

        ArrayList<String> wordsListStopped = new ArrayList<>();
        //Remove Digits
        file_data = file_data.replaceAll("\\d", "");
        //Remove URLs
        file_data = file_data.replaceAll("http.*?\\s", "");
        file_data = removeUrl(file_data);
        //Remove Characters
        file_data = file_data.replaceAll("[^\\w\\s]", "");
        //Trim
        file_data = file_data.trim().replaceAll("\\s+", " ");
        //To Lowercase
        file_data = file_data.toLowerCase();
        //Get Individual Words by spaces
        String[] words = file_data.split(" ");

        //Remove STOPWORDS plus ignored words
        for (String word : words) {
            wordsList.add(word);
        }
        for (String word : words) {
            String wordCompare = word.toLowerCase();
            if (!stopWordsSet.contains(wordCompare)) {
                wordsListStopped.add(word);
            }
        }
        //End of removing Stopwords
        return wordsListStopped;
    }

    /**
     *
     * @param file_loc
     * @param limit
     * @param important_words
     * @param notimpo_words
     * @return
     * @throws java.io.IOException
     */
    public static Map<String, Set> getIndex(String file_loc, Integer limit, String[] important_words, String[] notimpo_words) throws IOException {
        String doc_type = file_loc.substring(file_loc.lastIndexOf(".") + 1); //GET TYPE OF DOCUMENT
        String file_data = null;

        //ArrayList<String> wordsListStopped = null;
        Map<String, Set> map = new HashMap<>(); // Map for Word and Page Numbers
        Map<String, Integer> map_freq = new HashMap<>();

        if (null != doc_type) //CHECK IF FILE IS A PDF OR A DOCX            
        {
            switch (doc_type) {
                case "pdf":
                    int no_pages = getPDFPages(file_loc); //get number of pages in PDF File
                    for (int i = 1; i <= no_pages; i++) {
                        file_data = getPDF(file_loc, i); // Get String per page 
                        ArrayList<String> wordsListStopped = createArray(file_data, notimpo_words);  //preprocess text and put in Array                    
                        for (String w : wordsListStopped) {
                            //Get Frequency of each word into a seperate MAP "map_freq"
                            Integer n = map_freq.get(w);
                            n = (n == null) ? 1 : ++n;
                            map_freq.put(w, n);

                            //Inserting Words and Page Numbers into MAP
                            if (map.containsKey(w)) { // Checks if word already Exists
                                Set<Integer> temp_pgnum = new HashSet<>(); // Create a new Set of integers
                                temp_pgnum = (Set) map.get(w); //Collect what was previously in the map
                                temp_pgnum.add(i); // Append the new Page number to it
                                map.put(w, temp_pgnum); //Put back into Map
                                temp_pgnum = null;
                            } else {
                                //List<Integer> pgNum = new ArrayList<>();    
                                Set<Integer> pgNum = new HashSet<>(); // Create a new Set of integers
                                pgNum.add(i); // Append the new Page number to set
                                map.put(w, pgNum); // Put into Map  
                                pgNum = null;
                            }
                        }
                    }
                    //for (Map.Entry<String, Integer> entry : map_freq.entrySet()){
                    //System.out.println(entry.getKey() + " ---- " + entry.getValue());                                
                    //}
                    break;
                case "docx":
                    String new_pdf_loc = createPDFfromDocx(file_loc);
                    System.out.println(getPDF(new_pdf_loc, 2));
                    break;
            }
        }

        //Tag all entries with thier Part of Speech
        String noun = "NN";
        //Removing the non-words, words that has less than 3 characters
        map.entrySet().removeIf(e -> e.getKey().length() < 2);
        //Remove key if it occurs <= 2 times in the frequency map or as specified by the user
        if (important_words == null) {
            //remove from Map if the POSTag is not NOUN
            map.entrySet().removeIf(e -> !getPOStag(e.getKey()).trim().equals(noun));
            map.entrySet().removeIf(e -> map_freq.get(e.getKey()) <= limit);
        } else {
            map.entrySet().removeIf(e -> !getPOStag(e.getKey()).trim().equals(noun) && !Arrays.asList(important_words).contains(e.getKey()));
            //Remove key if it occurs <= 2 times in the frequency map or as specified by the user
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (!Arrays.asList(important_words).contains(pair.getKey().toString()) && map_freq.get(pair.getKey().toString()) <= limit) {
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
        // writePDF(file_loc, map);

        // Sort The Map Alphabetically
        map = sortByKeys(map);

        return map;
    }

    //Function to GET the Part Of Speech Tag for each Word
    public static String getPOStag(String toTag) {
        //String tagged = tagger.tagString(toTag);
        String tagged = tagger.tagString(toTag);
        String tag = tagged.substring(tagged.lastIndexOf("_") + 1);
        return tag;
    }

    //WRITING TO PDF FILE
    public static void writePDF(String file_location, Map<String, Set> map) throws IOException {
        PDFMergerUtility finalDoc = new PDFMergerUtility();
        PDDocument document = PDDocument.load(file_location);
        PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(0);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);

        for (Map.Entry<String, Set> entry : map.entrySet()) {
            contentStream.drawString(entry.getKey() + "- " + entry.getValue().toString());
        }

        contentStream.endText();
        contentStream.close();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            document.save(out);
        } catch (COSVisitorException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        finalDoc.addSource(new ByteArrayInputStream(out.toByteArray()));
        document.close();
    }

    //VERY VERY USELESS RIGHT NOW
    public static Map<String, String> getPOS(Map<String, Integer> old_map) {
        Map<String, String> new_map = new HashMap<>();

        Iterator it = old_map.entrySet().iterator();
        //
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String pos_ = getPOStag(pair.getKey().toString());
            String pos = pos_.substring(pos_.lastIndexOf("_") + 1);
            new_map.put(pair.getKey().toString(), pos);
            it.remove(); // avoids a ConcurrentModificationException
        }
        return new_map;
    }

    /*
     * Paramterized method to sort Map e.g. HashMap or Hashtable in Java
     * throw NullPointerException if Map contains null key
     */
    public static Map<String, Set> sortByKeys(Map<String, Set> map) {
        List<String> keys = new LinkedList<>(map.keySet());
        Collections.sort(keys);

        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<String, Set> sortedMap = new LinkedHashMap<>();

        for (String key : keys) {
            sortedMap.put(key, map.get(key));
        }

        return sortedMap;
    }

    public static void main(String[] args) {
        // TODO code application logic here
        //String file_loc = "C:\\Users\\PR0PH3T\\Desktop\\Omega\\CSC 423 - Concept of Programming Languages\\Presentation\\Chapter 1 423.docx";
        //String File_data = getText(file_loc);

        //Improve Look and Feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        home home_index = new home();
        home_index.show();

        //getPOStag("We are the world");
        //Map freq = getFreq(File_data, 5);
        //List words_list = new ArrayList<>(freq.keySet());
        //System.out.println("LIST:-> " + words_list);
        //List tag = getPOStag(words_list);
        //System.out.println(tag);
    }

}
