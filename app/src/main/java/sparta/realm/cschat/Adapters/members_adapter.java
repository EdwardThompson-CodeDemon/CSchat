package sparta.realm.cschat.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.Models.MemberImage;
import sparta.realm.cschat.R;
import sparta.realm.spartautils.svars;



public class members_adapter extends RecyclerView.Adapter<members_adapter.view> {

    Context cntxt;
    public ArrayList<Member> order_items;
    onItemClickListener listener;

    public interface onItemClickListener {

        void onItemClick(Member mem);
    }


    public members_adapter(Context cntxt, ArrayList<Member> order_items, onItemClickListener listener) {
        this.cntxt = cntxt;
        this.order_items = order_items;
        this.listener = listener;


    }

    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cntxt).inflate(R.layout.item_member, parent, false);

        return new view(view);
    }

    @Override
    public void onBindViewHolder(@NonNull view holder, int position) {
        Member obj = order_items.get(position);
        obj.profile_photo = Realm.databaseManager.loadObject(MemberImage.class, new Query().setTableFilters("member_transaction_no='" + obj.transaction_no + "'"));

        holder.member_name.setText(obj.name);
        holder.idno.setText(obj.email);
        holder.sid = (obj.sid);
        try {
            holder.person_pic.setImageURI(Uri.parse(Uri.parse(svars.current_app_config(cntxt).file_path_employee_data) + obj.profile_photo.image));

        } catch (Exception ex) {

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(obj);

            }
        });


    }


    @Override
    public int getItemCount() {
        return order_items.size();
    }

    public class view extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView member_name, idno, id_no_label;
        public String sid;
        public CircleImageView person_pic;


        view(View itemView) {
            super(itemView);

            member_name = itemView.findViewById(R.id.employee_name);
            idno = itemView.findViewById(R.id.idno);
            id_no_label = itemView.findViewById(R.id.no_of_workers_label);
            person_pic = itemView.findViewById(R.id.title_icon1);
            person_pic.setImageResource(R.drawable.ic_user);
            id_no_label.setText("");


        }

        @Override
        public void onClick(View view) {

        }
    }
}
