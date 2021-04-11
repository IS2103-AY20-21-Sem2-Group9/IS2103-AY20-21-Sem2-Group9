package ejb.session.stateless;

import Enumeration.AppointmentStatusEnum;
import entity.AppointmentEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AppointmentExistException;
import util.exception.AppointmentNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
@Local(AppointmentEntitySessionBeanLocal.class)
@Remote(AppointmentEntitySessionBeanRemote.class)
public class AppointmentEntitySessionBean implements AppointmentEntitySessionBeanRemote, AppointmentEntitySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;

    @Override
    public List<AppointmentEntity> retrieveAllAppointments() {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a");

        return query.getResultList();
    }
    
    @EJB
    private CustomerEntitySessionBeanLocal customerEntitysessionBeanLocal;
    @EJB
    private ServiceProviderEntitySessionBeanLocal serviceProviderEntitysessionBeanLocal;

    @Override
    public AppointmentEntity retrieveAppointmentByAppointmentNum(String appointmentNum) throws AppointmentNotFoundException {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a where a.appointmentNum = :appointmentNum");
        query.setParameter("appointmentNum", appointmentNum);

        try {
            return (AppointmentEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AppointmentNotFoundException("Appointment number " + appointmentNum + " does not exist!");
        }
    }

    @Override
    public void cancelAppointment(String appointmentNum) throws AppointmentNotFoundException {
        AppointmentEntity appointment = retrieveAppointmentByAppointmentNum(appointmentNum);
        em.remove(appointment);
    }

    
    @Override
    public AppointmentEntity createAppointmentEntity(AppointmentEntity apptEntity) throws UnknownPersistenceException, AppointmentExistException {
        try {
            CustomerEntity managedCustomer = customerEntitysessionBeanLocal.retrieveCustomerEntityById(apptEntity.getCustomerEntity().getId());
            ServiceProviderEntity managedSp = serviceProviderEntitysessionBeanLocal.retrieveServiceProviderByServiceProviderId(apptEntity.getServiceProviderEntity().getServiceProviderId());
            managedCustomer.getAppointments().add(apptEntity);
            managedSp.getAppointmentEntities().add(apptEntity);
            apptEntity.setCustomerEntity(managedCustomer);
            apptEntity.setServiceProviderEntity(managedSp);
            em.persist(apptEntity);
            em.flush();
            return apptEntity;
        } catch (PersistenceException | CustomerNotFoundException | ServiceProviderEntityNotFoundException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) { // A database-related exception
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) { // To get the internal error
                    throw new AppointmentExistException("Error creating appointment");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public void rateAppointment(AppointmentEntity appointmentEntity) {
        em.merge(appointmentEntity);
        em.flush();
    }
}
