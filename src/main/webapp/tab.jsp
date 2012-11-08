<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<link rel="shortcut icon" type="image/ico" href="http://www.sprymedia.co.uk/media/images/favicon.ico" />
		
		<title>DataTables example</title>
		<style type="text/css" title="currentStyle">
			@import "table/demo_page.css";
			@import "table/demo_table.css";
		</style>
		<script type="text/javascript" language="javascript" src="table/jquery.js"></script>
		<script type="text/javascript" language="javascript" src="table/jquery.dataTables.js"></script> 
		<script type="text/javascript" charset="utf-8">
			var oTable;
			var giRedraw = false;
			$(document).ready(function() {
			
				/* Add a click handler to the rows - this could be used as a callback */
				$("#example tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});
				
				/* Add a click handler for the delete row */
				$('#delete').click( function() {
					var anSelected = fnGetSelected( oTable );
					oTable.fnDeleteRow( anSelected[0] );
				} );
				
				/* Init the table */			
				oTable = $('#example').dataTable( {
					"bProcessing": true,
					"sAjaxSource": 'table/json_source.txt'
				} );
			} );
			
			/* Get the rows which are currently selected */
			function fnGetSelected( oTableLocal )
			{
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();
				
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass('row_selected') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}
		</script>
	</head>
	<body id="dt_example">
		<div id="container">
			<div class="full_width big">
				<i>DataTables</i> AJAX source example
			</div>

			<p><a href="javascript:void(0)" id="delete">Delete selected row</a></p>
			
			<h1>Live example</h1>
			<div id="dynamic">
<table cellpadding="0" cellspacing="0" border="0" class="display" id="example">
	<thead>
		<tr>
			<th width="20%">Rendering engine</th>
			<th width="25%">Browser</th>
			<th width="25%">Platform(s)</th>

			<th width="15%">Engine version</th>
			<th width="15%">CSS grade</th>
		</tr>
	</thead>
	<tbody>
		
	</tbody>
	<tfoot>
		<tr>

			<th>Rendering engine</th>
			<th>Browser</th>
			<th>Platform(s)</th>
			<th>Engine version</th>
			<th>CSS grade</th>
		</tr>

	</tfoot>
</table>
			</div>
			<div class="spacer"></div>
			
			
			<h1>Initialisation code</h1>
			<pre>$(document).ready(function() {
	$('#example').dataTable( {
		"bProcessing": true,
		"sAjaxSource": '../examples_support/json_source.txt'
	} );
} );</pre>
			
			
			<h1>Other examples</h1>
			<h2>Basic initialisation</h2>

			<ul>
				<li><a href="../basic_init/zero_config.html">Zero configuration</a></li>
				<li><a href="../basic_init/filter_only.html">Feature enablement</a></li>
				<li><a href="../basic_init/table_sorting.html">Sorting data</a></li>
				<li><a href="../basic_init/multi_col_sort.html">Multi-column sorting</a></li>
				<li><a href="../basic_init/multiple_tables.html">Multiple tables</a></li>

				<li><a href="../basic_init/hidden_columns.html">Hidden columns</a></li>
				<li><a href="../basic_init/dom.html">DOM positioning</a></li>
				<li><a href="../basic_init/state_save.html">State saving</a></li>
				<li><a href="../basic_init/alt_pagination.html">Alternative pagination styles</a></li>
				<li><a href="../basic_init/language.html">Change language information (internationalisation)</a></li>
				<li><a href="../basic_init/themes.html">ThemeRoller themes (Smoothness)</a></li>

			</ul>
			
			<h2>Advanced initialisation</h2>
			<ul>
				<li><a href="../advanced_init/events_pre_init.html">Events (pre initialisation)</a></li>
				<li><a href="../advanced_init/events_post_init.html">Events (post initialisation)</a></li>
				<li><a href="../advanced_init/column_render.html">Column rendering</a></li>
				<li><a href="../advanced_init/html_sort.html">Sorting without HTML tags</a></li>

				<li><a href="../advanced_init/dom_multiple_elements.html">Multiple table controls (sDom)</a></li>
				<li><a href="../advanced_init/dom_toolbar.html">Custom toolbar (element) around table</a></li>
				<li><a href="../advanced_init/sorting_control.html">Set sorting controls</a></li>
				<li><a href="../advanced_init/complex_header.html">Column grouping through col/row spans</a></li>
				<li><a href="../advanced_init/row_grouping.html">Row grouping</a></li>
				<li><a href="../advanced_init/row_callback.html">Row callback</a></li>

				<li><a href="../advanced_init/footer_callback.html">Footer callback</a></li>
				<li><a href="../advanced_init/language_file.html">Change language information from a file (internationalisation)</a></li>
			</ul>
			
			<h2>Data sources</h2>
			<ul>
				<li><a href="../data_sources/dom.html">DOM</a></li>
				<li><a href="../data_sources/js_array.html">Javascript array</a></li>

				<li><a href="../data_sources/ajax.html">Ajax source</a></li>
				<li><a href="../data_sources/server_side.html">Server side processing</a></li>
			</ul>
			
			<h2>Server-side processing</h2>
			<ul>
				<li><a href="../server_side/server_side.html">Obtain server-side data</a></li>
				<li><a href="../server_side/custom_vars.html">Add extra HTTP variables</a></li>

				<li><a href="../server_side/post.html">Use HTTP POST</a></li>
				<li><a href="../server_side/column_ordering.html">Custom column ordering (in callback data)</a></li>
				<li><a href="../server_side/pipeline.html">Pipelining data (reduce Ajax calls for paging)</a></li>
				<li><a href="../server_side/row_details.html">Show and hide details about a particular record</a></li>
				<li><a href="../server_side/select_rows.html">User selectable rows (multiple rows)</a></li>
			</ul>

			
			<h2>API</h2>
			<ul>
				<li><a href="../api/add_row.html">Dynamically add a new row</a></li>
				<li><a href="../api/multi_filter.html">Individual column filtering</a></li>
				<li><a href="../api/highlight.html">Highlight rows and columns</a></li>
				<li><a href="../api/row_details.html">Show and hide details about a particular record</a></li>

				<li><a href="../api/select_row.html">User selectable rows (multiple rows)</a></li>
				<li><a href="../api/select_single_row.html">User selectable rows (single row) and delete rows</a></li>
				<li><a href="../api/editable.html">Editable rows (with jEditable)</a></li>
				<li><a href="../api/form.html">Submit form with elements in table</a></li>
				<li><a href="../api/counter_column.html">Index column (static number column)</a></li>
				<li><a href="../api/show_hide.html">Show and hide columns dynamically</a></li>

				<li><a href="../api/regex.html">Regular expression filtering</a></li>
			</ul>
			
			<h2>Plug-ins</h2>
			<ul>
				<li><a href="../plug-ins/plugin_api.html">Add custom API functions</a></li>
				<li><a href="../plug-ins/sorting_plugin.html">Sorting and type detection</a></li>
				<li><a href="../plug-ins/paging_plugin.html">Custom pagination controls</a></li>

				<li><a href="../plug-ins/range_filtering.html">Range filtering / custom filtering</a></li>
				<li><a href="../plug-ins/dom_sort.html">Live DOM sorting</a></li>
			</ul>
			
			
			<p>Please refer to the <a href="http://www.datatables.net/"><i>DataTables</i> documentation</a> for full information about its API properties and methods.</p>
			
			
			<div id="footer" style="text-align:center;">

				<span style="font-size:10px;">DataTables &copy; Allan Jardine 2008-2010</span>
			</div>
		</div>
	</body>
</html>