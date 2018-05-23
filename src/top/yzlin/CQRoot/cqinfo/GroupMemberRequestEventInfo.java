package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;

final public class GroupMemberRequestEventInfo extends EventInfo {
    private String formGroup;

    protected GroupMemberRequestEventInfo(JSONObject text) {
        super(text);
    }

    final public String getFormGroup() {
        return formGroup;
    }
}
