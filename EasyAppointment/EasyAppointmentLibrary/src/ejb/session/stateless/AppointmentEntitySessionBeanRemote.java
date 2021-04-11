package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.ServiceProviderEntity;
import java.util.List;
import util.exception.AppointmentExistException;
import util.exception.AppointmentNotFoundException;
import util.exception.UnknownPersistenceException;

public interface AppointmentEntitySessionBeanRemote {
    
    public List<AppointmentEntity> retrieveAllAppointments();

    public void cancelAppointment(String appointmentNum) throws AppointmentNotFoundException;

    public AppointmentEntity retrieveAppointmentByAppointmentNum(String appointmentNum) throws AppointmentNotFoundException;
    
    public AppointmentEntity createAppointmentEntity(AppointmentEntity apptEntity) throws UnknownPersistenceException, AppointmentExistException;
  
    public void rateAppointment(AppointmentEntity appointmentEntity);
    
    public String retrieveAppointmentDateWithApptNum(String apptNum) throws AppointmentNotFoundException;
    
    public String retrieveAppointmentTimeWithApptNum(String apptNum) throws AppointmentNotFoundException;

    public void rateAppointment(long appointmentEntityId, int rating);
}
