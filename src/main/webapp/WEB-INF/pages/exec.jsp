<!-- commit diff is here ::: 
d03b1986bc2a7713f6a8d9dd3dfe5f135b7eb988
ac3021705f1233dd4253336fa8b8b6a19e8e1aae 
 -->
<!--  // @see ExecController.java -->
<form method="post">
	<table>
		<tr>
			<td><input type="submit">EXEC</input></td>
		</tr>
		<tr>
			<td>
				<!--   // @see ExecController.java ---> <textarea name="cmd"
					cols="77" rows="14" value="${testCMD}">${testCMD}</textarea>
			</td>
		</tr>		
		<tr> 
			<td>Output:</td>
		</tr>
		<tr>			
			<td><textarea cols="77" rows="4">${o}</textarea></td>
		</tr>		
		<tr>
			<td>LastCMD:</td>
		</tr>
		<tr>			
			<td width="80%"><textarea cols="77" rows="4" name="cmdLast" disabled="disabled">${cmdTmp}</textarea></td>
		</tr>
		<tr>
			<td width="10%" height="64">Tomcat RESULT Image:</td>
		</tr>
		<tr>			
			<td width="50%" height="25%">
			<IMG src="speed.gif" alt="tomcat/jee rrdoutput" width="99%" /></td>
		</tr>
		<tr>
			<td width="10%" height="64">Vector/SVG RESULT Image:</td>
		</tr>
		<tr>			
			<td width="100%" height="25%"><embed src="svg.jsp"  width="100%"
					type="image/svg+xml"   alt="SVG rrdoutput" />
			</td>
		</tr>

		<tr>
			<td><<<-- if u don't know what to do -- just try the default
				action - press the button ;)</td>
		</tr>
		<tr>
			<td><input type="submit">EXEC</input></td>
		</tr>
		<tr>
			<td><textarea cols="80" rows="3">${bufAsString}</textarea></td>
 
		</tr>
		
	</table>
</form>
