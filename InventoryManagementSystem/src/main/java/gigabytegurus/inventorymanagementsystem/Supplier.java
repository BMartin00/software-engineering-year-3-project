// A00316625 Nagasai Chintalapati
// A00318851 Martin  Banyanszki
// A00320456 Justas Zabitis
// A00317072 Hamza Hussain

package gigabytegurus.inventorymanagementsystem;

public class Supplier {
    private int supplierId;
    private String name;
    private String contact;

    public Supplier(int supplierId, String name, String contact)
    {
        this.supplierId = supplierId;
        this.name = name;
        this.contact = contact;
    }

    public int getSupplierId()
    {
    	return supplierId;
    }
    public String getName()
    {
    	return name;
    }
    public String getContact()
    {
    	return contact;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
