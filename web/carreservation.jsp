<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<div class="page-title">
    <div class="title_left">
        <h3>CAR RESERVATION</h3>
    </div>
</div>

<div class="col-md-6 col-sm-6 col-xs-12">
    <div class="add-new-btn">
        <span><i class="fa fa-plus-circle"></i>  NEW EVENT</span>
    </div>
</div>

<!-- Calendar Header -->
<div class="calendar-header col-md-12 col-sm-12 col-xs-12">
    <div class="col-md-5 col-sm-12 col-xs-12 calendar-filter-select">
        <div class="col-md-6 col-sm-12 col-xs-12">
            <label class="control-label col-md-0 col-sm-0 col-xs-0" for="car">
            </label>
            <select class="selectpicker show-tick show-menu-arrow" data-live-search="true" id="car"
                    name="car">
                <option value="all" selected>All Car</option>
                <c:forEach items="${requestScope.CARLIST}" var="dto">
                    <option value="${dto.carID}"
                            data-subtext="${dto.carBrand} - ${dto.carSlot} slot">${dto.carPlate}</option>
                </c:forEach>
            </select>
        </div>
    </div>
    <div class="col-md-7 col-sm-12 col-xs-12 pull-right calendar-button">
        <div>
            <div id="currentDate"></div>
        </div>
        <div class="calendar-nav-button">
            <button class="btn btn-primary" data-calendar-nav="prev"><i class="fas fa-arrow-circle-left"></i> Prev</button>
            <button class="btn btn-default active" data-calendar-nav="today">Today</button>
            <button class="btn btn-primary" data-calendar-nav="next">Next <i class="fas fa-arrow-circle-right"></i></button>
        </div>
        <div class="calendar-view-button">
            <button class="btn btn-warning active" data-calendar-view="month">Month</button>
            <button class="btn btn-warning" data-calendar-view="week">Week</button>
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
                    <button type="button" class="btn" id="event-delete">Delete</button>
                    <button type="button" class="btn" id="event-update">Edit</button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- #END Event detail modal -->

<!-- Event detail add/update modal-->
<div class="modal fade" id="event-details-modal" tabindex="-1" data-backdrop="static" data-keyboard="false"
     role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title"></h3>
            </div>
            <div class="modal-body">
                <form method="post" class="form-horizontal" action="#" id="eventDetailsForm">
                    <div id="event-details-body">
                        <div class="hiddeninfo" id="eventID"></div>
                        <div class="form-group">
                            <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                                <label class="control-label col-md-2 col-sm-2 col-xs-12" for="eventTitle">Title
                                </label>
                                <div class="col-md-10 col-sm-10 col-xs-12">
                                    <input type="text" id="eventTitle" name="eventTitle"
                                           class="form-control col-md-7 col-xs-12"
                                           data-validetta="required,maxLength[250]"
                                           data-vd-message-required="* Can't be empty.">
                                </div>
                            </div>
                        </div>

                        <div class="form-group" id="select-box-car">
                            <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                                <label class="control-label col-md-2 col-sm-2 col-xs-12" for="eventCar">Car
                                </label>
                                <div class="col-md-10 col-sm-10 col-xs-12 input-form">
                                    <select class="selectpicker show-tick show-menu-arrow"
                                            title="Select a car"
                                            data-validetta="required" data-vd-message-required="* Must be selected."
                                            id="eventCar" name="eventCar">
                                        <c:forEach items="${requestScope.CARLIST}" var="dto">
                                            <option value="${dto.carID}"
                                                    data-subtext="${dto.carBrand} - ${dto.carSlot} slot">${dto.carPlate}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                                <label class="control-label col-md-2 col-sm-2 col-xs-12" for="eventVenueOther">Venue
                                </label>
                                <div class="col-md-8 col-sm-7 col-xs-12 input-form">
                                    <input type="text" id="eventVenueOther" name="eventVenueOther"
                                           class="form-control col-md-7 col-xs-12"
                                           data-validetta="required,maxLength[250]" data-vd-message-required="* Can't be empty.">
                                </div>
                                <div class="col-md-2 col-sm-3 col-xs-3 input-form">
                                    <div id="showlocation"><i class="fas fa-map-marker-alt"></i></div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                                <label class="control-label col-md-2 col-sm-2 col-xs-12" for="eventDate">Date
                                </label>
                                <div class="col-md-8 col-sm-7 col-xs-12 input-form" id="eventDateParent">
                                    <input type="text" id="eventDate" name="eventDate"
                                           class="form-control col-md-7 col-xs-12" data-validetta="required"
                                           data-vd-message-required="* Can't be empty." readonly>
                                </div>
                                <div class="col-md-2 col-sm-3 col-xs-12 input-form check-form">
                                    <input type="checkbox" name="allDay" id="allDay" class="filled-in" checked>
                                    <label for="allDay">All Day</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                                <label class="control-label col-md-0 col-sm-0 col-xs-0" for="texteditor">
                                </label>
                                <textarea id="texteditor" name="eventContent"></textarea>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-12 col-sm-12 col-xs-12 btn-action">
                                <button type="button" class="btn btn-close btn-default" data-dismiss="modal">Close
                                </button>
                                <button type="submit" class="btn" id="detailsModalAction"
                                        data-loading-text="<i class='fa fa-spinner fa-spin'></i>  Processing">Submit
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- #END Event detail add/update modal-->

<!-- Venue location map modal-->
<div class="modal fade" data-backdrop="static" data-keyboard="false" id="venue-map-modal" tabindex="-1"
     role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">VENUE LOCATION MAP</h3>
            </div>
            <div class="modal-body">
                <div class="page-loader-wrapper">
                    <div class="loader">
                        <div class="preloader">
                            <div class="spinner-layer pl-red">
                                <div class="circle-clipper left">
                                    <div class="circle"></div>
                                </div>
                                <div class="circle-clipper right">
                                    <div class="circle"></div>
                                </div>
                            </div>
                        </div>
                        <p>Loading venue map .......</p>
                    </div>
                </div>
                <div class="hiddeninfo" id="apiLoadError"></div>
                <div id="map"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default btn-close" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<!-- #END Venue location map modal-->


<script src="js/carreservation.js"></script>