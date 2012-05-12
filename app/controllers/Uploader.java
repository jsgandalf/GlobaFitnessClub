package controllers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import play.mvc.Controller;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Sean
 * Date: 5/9/12
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class Uploader extends Controller{
    public static void requestHeader(){
    //    renderText("blah");
        String accessKey = "AKIAIIDVPNAYFEVBVVFA";
        String secretKey = "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";
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

//        s3.putObject()
        String l = s3.getBucketLocation("globafitnessphotos");
        renderText(l);
    }
//    private static String AWSAcessKeyID = "AKIAIIDVPNAYFEVBVVFA";
//
//    private static String generateAuthString() {
//        String stringToSign = "";
//        String signatureString = "etp7PXK4C9OVJBNA0L7HqwL4U4bHlh9PTnAeT9yi, "+stringToSign;
//        try {
//            byte[] utf8 = signatureString.getBytes("UTF-8");
//        }
//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String auth = "AWS"+" " + AWSAcessKeyID + ":" + signatureString;
//
//
//
//        return auth;
//    }
}