/*
* Lisensi Aku cinta kamu. Muaaah
                        / \
                       / _ \
                      | / \ |
                      ||   || _______
                      ||   || |\     \
                      ||   || ||\     \
                      ||   || || \    |
                      ||   || ||  \__/
                      ||   || ||   ||
                       \\_/ \_/ \_//
                      /   _     _   \
                     /               \
                     |    O     O    |
                     |   \  ___  /   |                           
                    /     \ \_/ /     \
                   /  -----  |  --\    \
                   |     \__/|\__/ \   |
                   \       |_|_|       /
                    \_____       _____/
                          \     /
                          |     |
* jelek ya kelinci nya. Peang pletat pletot gitu.
*/
package aiutils;

/**
 * Kelas untuk kelas tempat kuliah.
 * KELAS-CEPTION
 */
public class StudyRoom {
    private final short id;
    private final TimeConstraint room_c;
    
    /**
     * Konstruktor ruangan.
     * @param id Id ruangan. Misalnya 7602. (gimana kalo idnya misalnya, "MULTIMEDIA"?)
     * @param timeConstraint Konstrain waktu buka ruangan.
     */
    public StudyRoom(short id, TimeConstraint timeConstraint){
        this.id = id;
        room_c = timeConstraint;
    }
    
    /**
     * Get Room Id. Gak guna kan deskripsinya :p
     * @return id ruangan
     */
    public short getRoomId(){
        return id;
    }
    
    /**
     * Get Constrain. Ga guna kan deskripsinya :p
     * @return duplikat dari konstrain ruangan.
     */
    public TimeConstraint getConstraint(){
        return room_c.getCopy();
    }
    
}
