<%@page import="org.jrobin.cmd.RrdCommander"%>
<%@page import="org.jrobin.svg.RrdGraphInfo"%>
<!-- commit diff is here ::: 
d03b1986bc2a7713f6a8d9dd3dfe5f135b7eb988
ac3021705f1233dd4253336fa8b8b6a19e8e1aae 
 -->
<!--
<table > 
	<tr   >
		<td >
						<a href="https://rrdsaas.appspot.com/rrd.jsp"  title=" RRD Java impl ">
						RRDSAAS <%=System.currentTimeMillis() %>
	
		</td>
		<td height="64" width="64">
			img.svg:<img src="JavaLogo.svg" height="64" width="64" alt="javalogo as img.svg" /> 
		</td><td height="64" width="64">
			embed.svg :
			<embed src="JavaLogo.svg" type="image/svg+xml" height="64" width="64"  scale=true alt="javalogo as embed.svg" />
		</td><td height="64" width="64">	
			embed+p.svg :
			<embed src="JavaLogo.svg" height="64" width="64"  type="image/svg+xml" 
			scale=true alt="javalogo as embed+plug.svg"  pluginspage="http://www.adobe.com/svg/viewer/install/" /> 
		</td><td height="64" width="64">	
			object.svg :
			<object data="JavaLogo.svg" height="64" width="64" type="image/svg+xml" alt="javalogo as object+plug.svg" 
			codebase="http://www.adobe.com/svg/viewer/install/" />
			</a>
		</td><td height="64" width="64">	
			IMG-X :
			<IMG src="gif.jsp" height="64" width="64"   />
		</td> 	
	</tr>	
</table>
-->


					
		<%
			System.out.println("== RrdWS/RRDTool commander exec...");
			Object o = null;
			String cmdTmp = request.getParameter("cmd");
			java.io.ByteArrayOutputStream bufOut = new java.io.ByteArrayOutputStream ();
			java.io.PrintWriter parceOutputWriter = new java.io.PrintWriter(bufOut, true) ;
			try{ 
				if (cmdTmp != null) {
					System.out.println(cmdTmp);
					cmdTmp = cmdTmp.replace("\\", "\n");
					//RrdCommander.setRrdDbPoolUsed(false);
					o = RrdCommander.execute(cmdTmp);
					
					
					if (o instanceof org.jrobin.svg.RrdGraphInfo) {
						org.jrobin.svg.RrdGraphInfo oInf = (org.jrobin.svg.RrdGraphInfo) o;
						session.setAttribute("svg", oInf.getBytes());
					}
					if (o instanceof org.jrobin.graph.RrdGraphInfo) {
						org.jrobin.graph.RrdGraphInfo oInf = (org.jrobin.graph.RrdGraphInfo) o;
						session.setAttribute("gif", oInf.getBytes());
					}
				}
			}catch(Throwable e){
				e.printStackTrace(parceOutputWriter);
			}
			bufOut.flush();
			bufOut.close();
			%><td  ><%="<textarea>"+bufOut.toString()+"</textarea>"%></td><%			
		%> 			<td  >
						<textarea><%=o%></textarea>
					</td>	
				
				</tr>		
				<tr>
					<td>LastCMD:</td>
					<td width="80%">
						<textarea> <%=cmdTmp%> </textarea>
					</td>
			</tr > <tr > 
					
					<td width="10%" height="64">	
								Tomcat RESULT Image:
					</td>	<td width="10%" height="64">	
								GAE RESULT Image:
					</td>
			</tr > <tr > 
					<td width="10%" height="64">	
								<IMG src="speed.gif" height="64" width="164"  alt="tomcat/jee rrdoutput" />							
					</td>	<td width="10%" height="64">	
								<embed src="svg.jsp" type="image/svg+xml" height="100%" width="100%"  alt="gae rrdoutput" />
					</td>	
			</tr > <tr > 			
				<td>  
					<form method="post">
						<table>
							<tr > 
									<td>
										<%
										String testCOLOR = "FFFFFF000000CCAA";
										testCOLOR  = testCOLOR   .substring(  (int)(System.currentTimeMillis()%10));
										testCOLOR   = testCOLOR  .substring(0,6);
										String cccTMP = (""+cmdTmp).hashCode()%2==0?"graph":"graphsvg";
										String lnnTMP = ""+ (((int)(System.currentTimeMillis()%3)+1));
										String ttTMP = new String[]{"amazing","greate ", "fine", "super", "geil", "perfect", "bombastisch", "excelent", "ideal", "fantastic", "unique", "unreal"}[Math.abs((""+cmdTmp).hashCode()%11)];
										String vvvTMP = new String[]{"vip","vasja ", "pupkin", "vasilij", "ivanovich", "!no pasaran!", "bl-ky!", "Gra-Vi-Ca pa", "KinDzaDza", "Ma-Ma, Ma-Ma, ..", "Ky!", " "}[Math.abs((""+cmdTmp).hashCode()%11)];
										String testCMD = " rrdtool "+cccTMP + " speed.gif  -v '"+vvvTMP+"'  -t 'RRDWS is "+ttTMP+"!'  --start 920804400 --end 920808000  DEF:myspeed=test.rrd:speed:AVERAGE  LINE"+lnnTMP+":myspeed#"+testCOLOR;
										%> <textarea name="cmd"  cols="60" rows="4" value="<%=testCMD%>"><%=testCMD%></textarea>
									</td>
							</tr > <tr > 
									<td>	
										<<<-- if u don't know what to do -- just try the default action - press the button ;)  
									</td>	
							</tr > <tr > 
									<td>										
										<input type="submit" />
									</td>	
							</tr> 
						</table>
					</form>	
				</td>	<td width="10%" height="64">	
				</td>	
			</tr> 
		</table>		
	</td>
</tr>
</table>		
<!-- eo TUTORIAL -->