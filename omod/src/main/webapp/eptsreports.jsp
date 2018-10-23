<%@ include file="/WEB-INF/template/include.jsp"%>

    <%@ include file="/WEB-INF/template/header.jsp"%>

        <openmrs:htmlInclude file="/moduleResources/eptsreports/jquery.js" />

        <script type="text/javascript">
            function msgreg() {
                document.getElementById('msg').innerHTML = "<div id='openmrs_msg'>Registering...</div>";
                exit();
            }

            function msgrem() {
                document.getElementById('msg').innerHTML = "<div id='openmrs_msg'>Removing...</div>";
                exit();
            }
        </script>
        <style>
            table.reports {
                border-collapse: collapse;
                border: 1px solid blue;
                width: 100%;
            }
            
            .reports td {
                border-collapse: collapse;
                border: 1px solid blue;
            }
            
            .reports .tableheaders {
                font-weight: bold;
                background-color: #B0C4DE;
            }
            
            .reports .tabletd {
                font-weight: bold;
                background-color: #EEE;
            }
            
            .reports .alt {
                background-color: #B0C4DE;
            }
            
            .reports .altodd {
                background-color: #EEE;
            }
            
            .reports .hover {
                background-color: #DED;
            }
            
            .reports .althover {
                background-color: #EFE;
            }
        </style>
        <script type="text/javascript">
            $(document).ready(function() {
                $('tr:even').addClass('alt');
                $('tr:even').hover(
                    function() {
                        $(this).addClass('hover')
                    },
                    function() {
                        $(this).removeClass('hover')
                    }
                );
                $('tr:odd').addClass('altodd');
                $('tr:odd').hover(
                    function() {
                        $(this).addClass('althover')
                    },
                    function() {
                        $(this).removeClass('althover')
                    }
                );
            });
        </script>
        <div id="msg"></div>
        <h2>Register/Remove CMR Reports</h2>

        <br />
        <br />

        <table class="reports" style="width: 100%;">
            <tr class="tableheaders">
                <td>Report name</td>
                <td>Description</td>
                <td>Run</td>
                <td colspan="2">
                    <center>Action</center>
                </td>
            </tr>

            <tr>
                <td rowspan="1" class="tabletd">TX_NEW Report</td>
                <td>Number of adults and children newly enrolled on antiretroviral therapy (ART).</td>
                <td>Central</td>
                <td><a href="${pageContext.request.contextPath}/module/eptsreports/register_TXNEW.form" onclick=msgreg(this)>(Re) register</a></td>
                <td><a href="${pageContext.request.contextPath}/module/eptsreports/remove_TXNEW.form" onclick=msgrem(this)>Remove</a></td>
            </tr>
            <tr>
                <td rowspan="1" class="tabletd">TX_CURR Report</td>
                <td>Number of adults and children currently receiving antiretroviral therapy (ART)</td>
                <td>Central</td>
                <td><a href="${pageContext.request.contextPath}/module/eptsreports/register_TXCURR.form" onclick=msgreg(this)>(Re) register</a></td>
                <td><a href="${pageContext.request.contextPath}/module/eptsreports/remove_TXCURR.form" onclick=msgrem(this)>Remove</a></td>
            </tr>

        </table>
        <%@ include file="/WEB-INF/template/footer.jsp"%>