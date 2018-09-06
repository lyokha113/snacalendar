<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<div class="page-title">
    <div class="title_left">
        <h3>CALENDAR</h3>
    </div>
</div>

<!-- Calendar Header -->
<div class="calendar-header col-md-12 col-sm-12 col-xs-12">
    <div class="col-md-6 col-sm-12 col-xs-12">
        <div>
            <div id="stop-button"></div>
            <div id="play-button"></div>
        </div>
        <div id="timeout-changepage"></div>
    </div>
    <div class="col-md-6 col-sm-12 col-xs-12 pull-right calendar-button">
        <div>
            <div id="currentDate"></div>
        </div>
        <div class="calendar-view-button">
            <button class="btn btn-warning active" data-calendar-view="month" id="month-test">Month</button>
            <button class="btn btn-warning week-test" data-calendar-view="week">Week</button>
        </div>
    </div>
</div>
<!-- #END Calendar Header -->

<!-- Calendar -->
<div class="col-md-12 col-sm-12 col-xs-12">
    <div id="calendar"></div>
</div>
<!-- #END Calendar -->

<!-- Event detail modal -->
<div class="modal fade" id="events-modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title"></h3>
            </div>
            <div class="modal-body">
            </div>
            <div class="modal-footer">
                <div class="col-md-12 col-sm-12 col-xs-12 btn-action">
                    <button type="button" class="btn btn-close btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- #END Event detail modal -->

<script src="js/homepage.js"></script>