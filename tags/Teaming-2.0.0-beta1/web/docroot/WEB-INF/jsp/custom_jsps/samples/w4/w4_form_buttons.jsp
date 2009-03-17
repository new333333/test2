<style>
input.custom_submit {
  background-color: #009999;
  border: 1px solid #006666;
  color: #ffffff;
  font-size: 12px;
  padding: 0px 6px 0px 6px;
  cursor: pointer;
  white-space: nowrap;
}
</style>

<script language="JavaScript" type="text/javascript">
function mySubmit() {
    self.document.form1.title.value = self.document.form1.firstName.value + " " + self.document.form1.lastName.value;
}
</script>


<input type="hidden" name="title" value="" />

<input type="submit" class="custom_submit" name="okBtn" value="OK" onClick="ss_buttonSelect('okBtn');mySubmit();"/> 
<input type="submit" class="custom_submit" name="cancelBtn" value="Cancel" onClick="ss_buttonSelect('cancelBtn'); " />