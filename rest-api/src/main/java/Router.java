import java.util.Map ;
import java.util.HashMap ;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import org.apache.http.impl.client.HttpClients ;
import org.apache.http.impl.client.CloseableHttpClient ;
import org.apache.http.client.methods.HttpPost ;
import org.apache.http.entity.StringEntity ;
import org.apache.http.client.methods.CloseableHttpResponse ;
import com.fasterxml.jackson.databind.ObjectMapper ;

@RestController
@EnableAutoConfiguration
public class Router {
    static final String TEST_REMOTE_API="http://localhost:1111/rest-v1/test/remote" ;
    static final ObjectMapper om = new ObjectMapper () ;
    static class PreAuthorization {
        static Map create(Map req) throws Exception {
          // 1. parse the request
          System.out.println("[debug]req:" + req.toString()) ;
          String reqFileId = req.get("fileId").toString() ;

          // 2. make my request for remote server
          Map myReq= new HashMap() ;
          myReq.put("fileId", reqFileId) ;
             
          // 3. call remote api server 
          CloseableHttpClient client = HttpClients.createDefault();
          HttpPost httpPost = new HttpPost(TEST_REMOTE_API + "/preAuthorization") ;
          httpPost.setEntity(new StringEntity(om.writeValueAsString(myReq))) ;
          httpPost.setHeader("Accept", "application/json") ;
          httpPost.setHeader("Content-type", "application/json") ;
          CloseableHttpResponse httpResponse = client.execute(httpPost);
          Map remoteResp = om.readValue(httpResponse.getEntity().getContent(), Map.class) ;

          // 4. transform the remote result
          System.out.println("[debug]resp:" + remoteResp.toString()) ;
          Map remoteTransform = new HashMap() ;
          remoteTransform.put("remote-codeMessage", remoteResp.get("remote-codeMessage").toString()) ;
          remoteTransform.put("remote-fileid",  remoteResp.get("remote-fileid").toString()) ;

          // 5. return my response
          Map myResp = new HashMap() ;
          myResp.put("remote", remoteTransform) ;
          myResp.put("fileId", reqFileId) ;
          myResp.put("code", "0000") ;
          myResp.put("codeMessage", "success") ;
          return myResp ;
        }
    }

    @RequestMapping(value="/rest-v1/ping", method=RequestMethod.GET)
    String ping() {
    	return "pong\n";
    }

    @RequestMapping(value="/rest-v1/preAuthorization", method=RequestMethod.POST)
    @ResponseBody
    Map createPreAuthorization(@RequestBody Map req) {
      try {
    	return PreAuthorization.create(req) ;
      } catch (Exception e) {
        e.printStackTrace() ;
        return new HashMap() ;
      }
    }

    @RequestMapping(value="/rest-v1/test/remote/preAuthorization", method=RequestMethod.POST)
    @ResponseBody
    Map testRemoteCreatePreAuthorization(@RequestBody Map req) {
        Map remoteResp = new HashMap() ;
        remoteResp.put("remote-fileid", "remote:" + req.get("fileId").toString() ) ;
        remoteResp.put("remote-code", "0000") ;
        remoteResp.put("remote-codeMessage", "remote-codeMessage...") ;
        return remoteResp ;
    }
    
    public static void main(String[] args) throws Exception {
    	SpringApplication.run(Router.class, args);
    }
}
