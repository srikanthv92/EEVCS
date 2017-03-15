/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 JP iNFOTeCH
 */
package anon.util;

import java.io.UnsupportedEncodingException;

public class URLDecoder
{
	/**
	 * Decodes a URL to a unicode. This method uses UTF-8-encoding.
	 * @param a_strURL a URL in UTF-8-encoding
	 * @return the URL in unicode encoding
	 */
	public static String decode(String a_strURL)
	{
		if (a_strURL == null)
		{
			return null;
		}

		StringBuffer output = new StringBuffer();
		byte[] enc = new byte[a_strURL.length()];
		int bytes = 0;
		int i = 0;
		char c;

		try
		{
			while (i < a_strURL.length())
			{
				c = a_strURL.charAt(i);
				if (c == '+')
				{
					output.append(' ');
				}
				else if (c == '%')
				{
					enc[bytes] = (byte) Integer.parseInt(a_strURL.substring(i + 1, i + 3), 16);
					bytes++;
					i += 2;
				}
				else
				{
					output.append(c);
				}
				i++;

				if ( (i < a_strURL.length() && a_strURL.charAt(i) != '%') || i >= a_strURL.length())
				{
					output.append(new String(enc, 0, bytes, "UTF-8"));
					bytes = 0;
				}
			}
		}

		catch (NumberFormatException a_e)
		{
			return null;
		}
		catch (UnsupportedEncodingException a_e)
		{
			return null;
		}

		return output.toString();
	}
}
