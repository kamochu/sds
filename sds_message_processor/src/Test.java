
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 *
 * @author Samuel Kamochu
 */
public class Test {

    public static void main(String[] args) {

        long total = 0;
        for (int i = 0; i < 1; i++) {

            long start = System.currentTimeMillis();
            String swissNumberStr = "+254204444950";
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, null);
                System.out.println("Phone Number    : " + swissNumberProto);
                System.out.println("NDC Length      : " + phoneUtil.getLengthOfNationalDestinationCode(swissNumberProto));
                System.out.println("Number Type     : " + phoneUtil.getNumberType(swissNumberProto));

                //System.out.println(swissNumberProto);
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
            }
            long end = System.currentTimeMillis();

            total = (end - start);

        }
        System.out.println("Total: " + total);

    }
}
