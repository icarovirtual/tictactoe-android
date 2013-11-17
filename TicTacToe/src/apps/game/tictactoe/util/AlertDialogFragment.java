package apps.game.tictactoe.util;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import apps.games.tictactoe.BoardActivity;

@SuppressLint("NewApi")
public class AlertDialogFragment extends DialogFragment
{
	public static AlertDialogFragment newInstance(Bundle dialogBundle)
	{
		AlertDialogFragment frag = new AlertDialogFragment();
        frag.setArguments(dialogBundle);
        return frag;
	}

	@Override
	public android.app.Dialog onCreateDialog(android.os.Bundle savedInstanceState)
	{
		return new AlertDialog.Builder(getActivity())
		.setPositiveButton(getArguments().getCharSequence("positive"), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				((BoardActivity)getActivity()).doPositiveClick();
			}
		})
		.setNegativeButton(getArguments().getCharSequence("negative"), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				((BoardActivity)getActivity()).doNegativeClick();
			}
		})
		.setMessage(getArguments().getCharSequence("state"))
		.setTitle("Game over")
		.create();
	};
}
