package top.yzlin.CQRoot.cqinfo;

final public class GroupMemberRequestEventInfo extends EventInfo {
    final public static int SUBTYPE_APPLY = 1;
    final public static int SUBTYPE_OWN_APPLY = 2;

    private String formGroup;

    public String getFormGroup() {
        return formGroup;
    }

    public void setFormGroup(String formGroup) {
        this.formGroup = formGroup;
    }
}
