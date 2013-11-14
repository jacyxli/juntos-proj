package org.vamosjuntos.formedittextvalidator;

import android.widget.EditText;

public class AndValidator extends MultiValidator {
  public AndValidator( Validator ... validators) {
    super(null, validators);
  }
  public AndValidator() {
    super(null);
  }
  
  public boolean isValid(EditText et) {
    for (Validator v: validators) {
      if (! v.isValid(et)) {
        this.errorMessage = v.getErrorMessage();
        return false; // Remember :) We're acting like an || operator.
      }
    }
    return true;
  }
 }

