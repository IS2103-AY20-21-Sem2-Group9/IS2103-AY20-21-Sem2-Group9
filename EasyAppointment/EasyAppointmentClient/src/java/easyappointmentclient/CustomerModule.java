package easyappointmentclient;

import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.CustomerEntitySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.AppointmentEntity;
import entity.BusinessCategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class CustomerModule {
    private CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote;
    private ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    private CustomerEntity loggedInCustomerEntity;

    public CustomerModule() {
    }

    public CustomerModule(CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote, CustomerEntity loggedInCustomerEntity) {
        this.customerEntitySessionBeanRemote = customerEntitySessionBeanRemote;
        this.loggedInCustomerEntity = loggedInCustomerEntity;
    }
    
    public void mainMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** Admin Terminal :: Main ***\n");
            System.out.println("You are now logged in as " + loggedInCustomerEntity.getFirstName() + " " + loggedInCustomerEntity.getLastName() + "\n");
            System.out.println("1: Search Operation");
            System.out.println("2: Add Appointments");
            System.out.println("3: View Appointments");
            System.out.println("4: Cancel Appointments");
            System.out.println("5: Rate Service Provider");
            System.out.println("6: Logout");
            
            response = 0;
            
            while (response < 1 || response > 6)
            {
                System.out.print("> ");
                response = scanner.nextInt();
                
                if (response == 1) 
                {
                    searchOperation();
                    
                } 
                else if (response == 2) 
                {
                    addAppointments();
                }
                else if (response == 3) 
                {
                    viewAppointments();
                }
                else if (response == 4) 
                {
                    cancelAppointments();
                }
                else if (response == 5) 
                {
                    rateServiceProvider();
                }
                else if (response == 6) 
                {
                    break;
                }
                else 
                {
                    System.out.println("Please key in 1 ~ 6 only.");
                }
            }
            
            if (response == 6) 
            {
                System.out.println("Thank you! Logging out...\n");
                break;
            }
        }
    }
    
    public List<ServiceProviderEntity> searchOperation() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** Customer Terminal :: Search Operation ***\n");
        
        // Get all the biz categories and print them
        List<BusinessCategoryEntity> businessCategories = this.businessCategorySessionBeanRemote.retrieveAllBusinessCategories();
        for (int i = 0; i < businessCategories.size(); i++) { 
            System.out.print(i + 1);
            System.out.println(" ");
            System.out.println(businessCategories.get(i).getCategoryName());
            System.out.println(" | ");
        }
        
        
        System.out.print("Enter business category> ");
        Integer category = sc.nextInt(); 
        sc.nextLine();
        System.out.print("Enter city> ");
        String city = sc.nextLine();
        
        System.out.print("Enter date> ");
        String dateStr = sc.nextLine();
        LocalDate date = LocalDate.parse(dateStr); // e.g. yyyy-MM-dd
        
        // Print headers
        System.out.printf("%20s | %-15s | %-20s | %-15s | %-15s", "Service Provider Id", "Name", "First available Time", "Address", "Overall rating");
        
        // Unimplemented SP session bean method
        List<ServiceProviderEntity> serviceProviders = this.serviceProviderEntitySessionBeanRemote.retrieveAllAvailableServiceProviderForTheDay(category, city, date);
        
        // Print record rows
        for (int i = 0; i < serviceProviders.size(); i++) {
            Long spId = serviceProviders.get(i).getServiceProviderId();
            String name = serviceProviders.get(i).getName();
            // Get avail time - firstAvai (String)
            String address = serviceProviders.get(i).getAddress();
            // Get rating value - rating (Integer)
            
            System.out.printf("%-20d | %-15s | %-20s | %-15s | %d", spId, "Name", firstAvai, "Address", rating);
        }
        
         

        
        return serviceProviders; 
    }
    
    public void addAppointments() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** Customer Terminal :: Add Appointments ***\n");        
        List<ServiceProviderEntity> serviceProviders = this.searchOperation();
        
        System.out.println("Enter 0 to go back to the previous menu.");
        if (sc.nextInt() == 0) {
            return; // Break out of the method
        }
        
        System.out.print("Service provider Id> ");
        Integer spId = sc.nextInt();
        System.out.println();
        
        System.out.println("Available Appointment slots:");
        // Print out all available appointment slots here e.g. 
        // 11:30 | 12:30 | 1:30 | 2:30 | 4:30 | 5:30
        
        
        System.out.println("Enter 0 to go back to previous menu");
        if (sc.nextInt() == 0) {
            return;
        }
        
        System.out.print("Enter time> ");
        String timeStr = sc.nextLine(); // e.g. 11:30
        LocalTime time = LocalTime.parse(timeStr);
        
        System.out.println("The appointment with %s at %s on %s is confirmed.");
        
    }
    
    public void viewAppointments() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** Customer Terminal :: View Appointments ***\n");
        
        List<AppointmentEntity> appointments = this.loggedInCustomerEntity.getAppointments();
        
        if (!appointments.isEmpty()) {
            
            for (int i = 0; i < appointments.size(); i++) {
                AppointmentEntity appt = appointments.get(i);
                
                System.out.print(appt.getAppointmentNum());
                System.out.print(" | ");
                System.out.print(appt.getAppointmentTime());
                System.out.print(" | ");
                System.out.print(appt.getAppointmentStatusEnum());
                System.out.println();
            }
        } else {
            System.out.println("There are currently no appointments.");
        }
    }
    
    public void cancelAppointments() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** Customer Terminal :: Cancel Appointments ***\n");
    }
    
    public void rateServiceProvider() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** Customer Terminal :: Rate Service Provider ***\n");
    }
}
