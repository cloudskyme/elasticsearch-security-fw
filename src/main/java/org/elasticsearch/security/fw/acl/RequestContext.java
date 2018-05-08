package org.elasticsearch.security.fw.acl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.CompositeIndicesRequest;
import org.elasticsearch.action.IndicesRequest;
import org.elasticsearch.common.util.ArrayUtils;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.security.fw.common.SecurityPermissionException;

/**
 * Created by sscarduzio on 20/02/2016.
 */
public class RequestContext {
  /*
    * A regular expression to match the various representations of "localhost"
    */
  private final static Pattern localhostRe = Pattern.compile("^(127(\\.\\d+){1,3}|[0:]+1)$");

  private final static String LOCALHOST = "127.0.0.1";

  private final RestChannel channel;
  private final RestRequest request;
  private final String action;
  private final ActionRequest actionRequest;
  private String[] indices = null;
  private String content = null;

  public RequestContext(RestChannel channel, RestRequest request, String action, ActionRequest actionRequest) {
    this.channel = channel;
    this.request = request;
    this.action = action;
    this.actionRequest = actionRequest;
  }

  public String getRemoteAddress() {
    String remoteHost = ((InetSocketAddress) request.getRemoteAddress()).getAddress().getHostAddress();
    // Make sure we recognize localhost even when IPV6 is involved
    if (localhostRe.matcher(remoteHost).find()) {
      remoteHost = LOCALHOST;
    }
    return remoteHost;
  }

  public String getContent() {
    if(content == null){
      try {
        content = request.content().utf8ToString();
      } catch (Exception e) {
        content = "<not available>";
      }
    }
    return content;
  }

  public String[] getIndices() {
    if (indices != null) {
      return indices;
    }
    final String[][] out = {new String[1]};
    AccessController.doPrivileged(
        new PrivilegedAction<Void>() {
          @Override
          public Void run() {
            String[] indices = new String[0];
            ActionRequest ar = actionRequest;
            if (ar instanceof CompositeIndicesRequest) {
              CompositeIndicesRequest cir = (CompositeIndicesRequest) ar;
              ArrayList<String> tmpAry = new ArrayList<String>();
              for (IndicesRequest ir : cir.subRequests()) {
                 indices = ArrayUtils.concat(indices, ir.indices(), String.class);
              }
              // Dedupe indices
              HashSet<String> tempSet = new HashSet<>(Arrays.asList(indices));
              indices = tempSet.toArray(new String[tempSet.size()]);
            } else {
              try {
                Method m = ar.getClass().getMethod("indices");
                if(m.getReturnType() != String[].class){
                  out[0] = new String[]{};
                  return null;
                }
                m.setAccessible(true);
                indices = (String[]) m.invoke(ar);
              } catch (SecurityException e) {
                throw new SecurityPermissionException("Insufficient permissions to extract the indices. Abort! Cause: " + e.getMessage(), e);
              } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
              }
            }

            if(indices == null) {
              indices = new String[0];
            }
            
            out[0] = indices;
            return null;
          }
        }
    );

    indices = out[0];
    return out[0];
  }

  public RestChannel getChannel() {
    return channel;
  }

  public RestRequest getRequest() {
    return request;
  }

  public String getAction() {
    return action;
  }

  public ActionRequest getActionRequest() {
    return actionRequest;
  }

  @Override
  public String toString() {
    StringBuilder idxsb = new StringBuilder();
    idxsb.append("[");
    for(String i : getIndices()){
      idxsb.append(i).append(' ');
    }
    String idxs = idxsb.toString().trim() + "]";
    return "{ action: " + action +
        ", OA:" + getRemoteAddress() +
        ", indices:" + idxs+
        ", M:" + request.method() +
        ", P:" + request.path() +
        ", C:" + getContent() +
        ", Headers:" + request.headers() +
        "}" ;
  }

}