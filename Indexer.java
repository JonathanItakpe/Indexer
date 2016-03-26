/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import com.snowtide.PDF;
import com.snowtide.io.*;

import com.snowtide.pdf.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.*;
import java.util.*;
import java.util.Arrays.*;
import java.util.stream.Stream;
import static java.util.stream.StreamSupport.stream;
import javax.swing.JOptionPane;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.util.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

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

    /**
     * @param filePath
     * @return
     */

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
    
    //GETTING PDF STRING USING PDFxStream
    public static String getPDFText (String pdfFile) throws IOException {
        Document pdf = PDF.open(pdfFile);
        //pdf.getPage(2);
        StringWriter buffer = new StringWriter();
        pdf.pipe(new OutputTarget(buffer));
        pdf.close();
        return buffer.toString();
    }
    
    //GET NUMBER OF PAGES IN PDF FILE
    public static int getPDFPages (String fileLoc) throws IOException {
        PDDocument doc = PDDocument.load(new File(fileLoc));
        int count = doc.getNumberOfPages();
        return count;
    }
    
        //GETTING PDF STRING USING PDFBOX
    public static String getPDF (String fileLoc, int pageNumber) {
        PDDocument pdf = null;
        String parsedText = null;
        COSDocument cosDoc = null;
        //BufferedWriter br = null;
        try{
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
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return parsedText;
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
    public static Map<String, String> getFreq(String file_loc, Integer limit, String[] important_words, String[] notimpo_words) throws IOException {
        String doc_type = file_loc.substring(file_loc.lastIndexOf(".") + 1); //GET TYPE OF DOCUMENT
        String file_data = null;
        int no_pages = getPDFPages(file_loc); //get number of pages in PDF File
        
        if (null != doc_type) //CHECK IF FILE IS A PDF OR A DOCX            
        //CHECK IF FILE IS A PDF OR A DOCX
        switch (doc_type) {
            case "pdf":
                file_data = getPDF(file_loc, no_pages);
                break;
            case "docx":
                file_data = getText(file_loc);
                break;
        }
        
        ArrayList<String> wordsList = new ArrayList<>();
        
        //get set of not important words of it is NOT empty
        if (notimpo_words != null) {
            // COnvert Ingnored words to Set  
            Set<String> _ignored = new HashSet<>(Arrays.asList(notimpo_words));
            stopWordsSet.addAll(_ignored); //add to be ignored words to the stopwords
        }

                     
        ArrayList<String> wordsListStopped = new ArrayList<>();
        //Remove Characters
        file_data = file_data.replaceAll("[^\\w\\s]", "");
        //Remove Digits
        file_data = file_data.replaceAll("\\d", "");
        //Remove URLs
        file_data = file_data.replaceAll("http.*?\\s", "");
        //Trim
        file_data = file_data.trim().replaceAll("\\s+", " ");
        //To Lowercase
        file_data = file_data.toLowerCase();
        //Get Individual Words
        String[] words = file_data.split(" ");

            //Sort Alphabetically
        //Arrays.sort(words);
        
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
        
        
        //Creating Frequency Map
        //System.out.println(words);
        Map<String, Integer> map = new HashMap<>();
        for (String w : wordsListStopped) {
            Integer n = map.get(w);
            n = (n == null) ? 1 : ++n;
            map.put(w, n);
        }

        //Tag all entries with thier Part of Speech
        Map<String, String> new_map = getPOS(map);
        
        String noun = "NN";
        if (important_words == null) {
            //Remove key if it occurs less than 10 times
            new_map.entrySet().removeIf(e -> !e.getValue().trim().equals(noun));
        } else {
            new_map.entrySet().removeIf(e -> !e.getValue().trim().equals(noun) && !Arrays.asList(important_words).contains(e.getKey()));
            //!Arrays.asList(important_words).contains(e.getKey()) || 
        }

        return new_map;
    }

    public static String getPOStag(String toTag) {
        //String tagged = tagger.tagString(toTag);
        String tagged = tagger.tagString(toTag);
        return tagged;
    }

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
