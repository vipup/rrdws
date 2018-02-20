<!-- commit diff is here ::: 
d03b1986bc2a7713f6a8d9dd3dfe5f135b7eb988
ac3021705f1233dd4253336fa8b8b6a19e8e1aae 
 -->
<!--  // @see ExecController.java -->
<form method="post">
	<table>
		<tr>
			<td><textarea cols="80" rows="10">${bufAsString}</textarea></td>
			<td><textarea>${o}</textarea></td>
		</tr>
		<tr>
			<td>LastCMD:</td>
			<td width="80%"><textarea>${cmdTmp}</textarea></td>
		</tr>
		<tr>
			<td width="10%" height="64">Tomcat RESULT Image:</td>
			<td width="10%" height="64">GAE RESULT Image:</td>
		</tr>
		<tr>
			<td width="10%" height="64"><IMG src="speed.gif" height="64"
				width="164" alt="tomcat/jee rrdoutput" /></td>
			<td width="10%" height="64"><embed src="svg.jsp"
					type="image/svg+xml" height="100%" width="100%" alt="gae rrdoutput" />
			</td>
		</tr>
		<tr>
			<td>
				<table>
					<tr>
						<td>
							<!--   // @see ExecController.java ---> <textarea name="cmd"
								cols="60" rows="4" value="${testCMD}">${testCMD}</textarea>
						</td>
					</tr>
					<tr>
						<td><<<-- if u don't know what to do -- just try the default
							action - press the button ;)</td>
					</tr>
					<tr>
						<td><input type="submit">EXEC</input></td>
					</tr>
				</table>

			</td>

			</td>
		</tr>
	</table>
</form>
<!-- eo TUTORIAL -->