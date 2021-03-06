package tetris.view.listener;

import org.json.JSONObject;
import tetris.view.ViewName;

/**
 * view切換事件.
 *
 * @author Ray Lee
 */
public interface OnChangeViewListener {
  /**
   * 當有view切換事件時.
   *
   * @param view 即將前往的場景名稱
   * @param params 參數
   */
  public void onChangeView(ViewName view, JSONObject params);
}
