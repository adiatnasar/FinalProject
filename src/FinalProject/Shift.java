package FinalProject;
public class Shift
{
    String staffID;
    String date;
    String startTime;
    String endTime;
    
    // Shift Class
    public Shift(String staffID,String date,String startTime,String endTime)
    {
        this.staffID=staffID;
        this.date=date;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    public String toCSV()
    {
        return staffID+","+date+","+startTime+","+endTime;
    }
}